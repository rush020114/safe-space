package com.safespace.content_filter_backend.admin.controller;

import com.safespace.content_filter_backend.admin.service.SseEmitterService;
import com.safespace.content_filter_backend.report.dto.ReportDTO;
import com.safespace.content_filter_backend.report.service.ReportService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/reports")
public class AdminReportController {
  private final ReportService reportService;
  private final SseEmitterService sseEmitterService;

  // produces 값이 없으면 클라이언트가 EventSource로 연결해도 SSE로 인식되지 않음
  // 반드시 getMapping으로 요청을 보내야 함.
  /**
   * SSE 연결 엔드포인트
   * GET /admin/reports/stream
   */
  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public SseEmitter streamReports(@AuthenticationPrincipal UserDetails user){
    String adminId = user.getUsername();
    return sseEmitterService.createSseEmitter(adminId);
  }

  // 신고 목록 조회
  // targetType(POST/COMMENT)에 따라 조인 테이블과 SELECT 컬럼을 동적으로 분기함(신고 유형 확장성 고려)
  @GetMapping("/{targetType}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getReportListForAdmin(@PathVariable("targetType") String targetType){
    try {
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(reportService.getReportListForAdmin(targetType));
    } catch (IllegalArgumentException e) {
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
  @PutMapping("/{reportId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> handleReport(
          @PathVariable int reportId,
          @RequestBody ReportDTO reportDTO
  ) {
    try {
      reportDTO.setReportId(reportId);
      reportService.handleReport(reportDTO);
      return ResponseEntity
              .status(HttpStatus.OK)
              .body("신고 처리 완료" + reportDTO.getReportStatus());
    } catch (IllegalArgumentException e){
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
