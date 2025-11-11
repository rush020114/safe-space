package com.safespace.content_filter_backend.report.controller;

import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.report.dto.ReportDTO;
import com.safespace.content_filter_backend.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("reports")
public class ReportController {
  private final ReportService reportService;
  private final JwtUtil jwtUtil;

  // 신고 등록
  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> regReport(
          @RequestBody ReportDTO reportDTO
          , @RequestHeader("Authorization") String token
  ){
    try {
      String jwt = token.replace("Bearer ", "");
      reportDTO.setReporterId(jwtUtil.getMemIdFromToken(jwt));
      log.info("report jwt 토큰 : {}", jwt);

      // 신고 등록
      reportService.regReport(reportDTO);
      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body("신고 접수 완료");
    } catch (RuntimeException e){
      log.info("신고 등록 실패 - 입력 오류 : {}", e.getMessage());
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(e.getMessage());
    } catch (Exception e){
      log.info("신고 등록 실패 - 서버 오류", e);
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(e.getMessage());
    }
  }
}
