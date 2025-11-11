package com.safespace.content_filter_backend.report.mapper;

import com.safespace.content_filter_backend.report.dto.ReportDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {
  // 신고 등록
  void regReport(ReportDTO reportDTO);

  // 신고 중복
  int getDupReport(ReportDTO reportDTO);

  // 관리자 신고 조회
  void getReportListForAdmin(String targetType);
}
