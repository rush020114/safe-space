package com.safespace.content_filter_backend.domain.report.model;

public enum ReportStatus {
  PENDING
  , APPROVED
  , REJECTED;

  public static ReportStatus from (String value){
    System.out.println("신고 상태값 수신: [" + value + "]");
    // 신고 상태값 빈문자열이면
    if(value == null || value.trim().isEmpty())
      throw new IllegalArgumentException("신고 상태값은 null 또는 빈문자열일 수 없습니다.");
    try {
      return ReportStatus.valueOf(value.trim().toUpperCase());
    } catch (Exception e){
      throw new IllegalArgumentException("지원하지 않는 신고 상태값입니다. : " + value);
    }
  }
}
