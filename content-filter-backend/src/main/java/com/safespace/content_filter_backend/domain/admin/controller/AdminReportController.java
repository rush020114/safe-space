package com.safespace.content_filter_backend.domain.admin.controller;

import com.safespace.content_filter_backend.auth.dto.CustomUserDetails;
import com.safespace.content_filter_backend.domain.admin.service.SseEmitterService;
import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.domain.report.dto.ReportDTO;
import com.safespace.content_filter_backend.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "관리자 신고 관리", description = "관리자가 신고 목록을 조회하고 처리하며, SSE로 실시간 알림을 수신합니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/reports")
public class AdminReportController {
  private final ReportService reportService;
  private final SseEmitterService sseEmitterService;
  private final JwtUtil jwtUtil;

  // produces 값이 없으면 클라이언트가 EventSource로 연결해도 SSE로 인식되지 않음
  // 반드시 getMapping으로 요청을 보내야 함.
  /**
   * SSE 연결 엔드포인트
   * GET /admin/reports/stream
   */
  @Operation(
          summary = "신고 실시간 알림 (SSE)",
          description = "관리자가 실시간으로 신고 알림을 수신합니다. Swagger에서는 테스트 불가하며, 프론트에서 EventSource로 구현되어야 합니다."
  )
  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public SseEmitter streamReports(@AuthenticationPrincipal UserDetails user){
    String adminId = user.getUsername();
    return sseEmitterService.createSseEmitter(adminId);
  }

  // 신고 목록 조회
  // targetType(POST/COMMENT)에 따라 조인 테이블과 SELECT 컬럼을 동적으로 분기함(신고 유형 확장성 고려)
  @Operation(
          summary = "신고 목록 조회",
          description = "신고 대상 유형(POST 또는 COMMENT)에 따라 목록을 조회합니다."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "신고 목록 조회 성공"),
          @ApiResponse(responseCode = "400", description = "잘못된 신고 유형"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @Parameter(name = "targetType", description = "신고 대상 유형", example = "POST")
  @GetMapping("/{targetType}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getReportListForAdmin(@PathVariable("targetType") String targetType){
    try {
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(reportService.getReportListForAdmin(targetType));
    } catch (RuntimeException e) {
      log.info("신고 목록 조회 실패 - 타입 불일치 : {}", e.getMessage());
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(e.getMessage());
    } catch (Exception e) {
      log.info("신고 목록 조회 실패 - 서버 오류", e);
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("신고 목록 조회 중 서버 오류 발생");
    }
  }

  // 신고 처리
  @Operation(
          summary = "신고 처리",
          description = "관리자가 신고를 처리합니다. 처리 상태는 Enum으로 관리되며, 신고 ID와 처리 정보를 전달합니다."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "신고 처리 완료"),
          @ApiResponse(responseCode = "400", description = "입력 오류"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @Parameter(name = "reportId", description = "신고 ID", example = "42")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "신고 처리 정보 DTO")
  @PutMapping("/{reportId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> handleReport(
          @PathVariable int reportId
          , @RequestBody ReportDTO reportDTO
          , @AuthenticationPrincipal CustomUserDetails user
          ) {
    try {
      int adminId = user.getUserId();
      reportDTO.setReportId(reportId);
      reportService.handleReport(reportDTO, adminId);
      return ResponseEntity
              .status(HttpStatus.OK)
              .body("신고 처리 완료" + reportDTO.getReportStatus());
    } catch (RuntimeException e){
      log.info("신고 처리 실패 - 타입 불일치 : {}", e.getMessage());
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(e.getMessage());
    } catch (Exception e) {
      log.info("신고 처리 실패 - 서버 오류", e);
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("신고 처리 중 서버 오류 발생");
    }
  }

}
