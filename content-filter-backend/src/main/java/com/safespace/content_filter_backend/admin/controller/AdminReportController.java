package com.safespace.content_filter_backend.admin.controller;

import com.safespace.content_filter_backend.admin.service.SseEmitterService;
import com.safespace.content_filter_backend.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
  public SseEmitter streamReports(@AuthenticationPrincipal UserDetails user){
    String adminId = user.getUsername();
    return sseEmitterService.createSseEmitter(adminId);
  }


}
