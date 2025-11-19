package com.safespace.content_filter_backend.domain.report.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "신고 관리", description = "이용자 신고 등록 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("reports")
public class ReportController {
  private final ReportService reportService;
  private final JwtUtil jwtUtil;

  // 신고 등록
  @Operation(
          summary = "신고 등록",
          description = "이용자가 게시글 또는 댓글에 대해 신고를 등록합니다. JWT 토큰을 통해 신고자 ID를 추출합니다."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "신고 접수 완료"),
          @ApiResponse(responseCode = "400", description = "입력 오류"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> regReport(
          @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "신고 정보 DTO")
          @RequestBody ReportDTO reportDTO,
          @Parameter(description = "JWT 인증 토큰", example = "Bearer ...")
          @RequestHeader("Authorization") String token
  ){
    String jwt = token.replace("Bearer ", "");
    reportDTO.setReporterId(jwtUtil.getMemIdFromToken(jwt));
    log.info("report jwt 토큰 : {}", jwt);

    // 신고 등록
    reportService.regReport(reportDTO);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("신고 접수 완료");
  }
}
