package com.safespace.content_filter_backend.report.service;

import com.safespace.content_filter_backend.report.dto.ReportDTO;
import com.safespace.content_filter_backend.report.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final ReportMapper reportMapper;

  // 신고 등록
  public void regReport(ReportDTO reportDTO){
    String reason = reportDTO.getReportReason();

    // 중복 신고라면
    if(reportMapper.getDupReport(reportDTO) == 1){
      throw new RuntimeException("중복된 신고입니다.");
    }

    // 신고 사유가 없다면
    if(reason == null || reason.isEmpty())
      throw new RuntimeException("신고 사유를 선택해주십시오");

    reportMapper.regReport(reportDTO);
  }

  // 관리자 신고 조회
  public void getReportListForAdmin(String targetType){
    if(targetType == null || (!targetType.equals("POST") && !targetType.equals("COMMENT"))){

    }
  }
}
