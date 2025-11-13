package com.safespace.content_filter_backend.domain.report.model;

public enum ReportTargetType {
  POST
  , COMMENT;

  public static ReportTargetType from (String value){
    // 신고 유형이 빈문자열이면
    if(value == null || value.trim().isEmpty()){
      throw new IllegalArgumentException("신고 유형은 null 또는 빈 문자열일 수 없습니다.");
    }
    try {
      // 대문자로 변환한 value가 POST 또는 COMMENT가 아니면 예외 발생
      return ReportTargetType.valueOf(value.trim().toUpperCase());
    } catch (Exception e){
      throw new IllegalArgumentException("지원하지 않는 신고 유형입니다. : " + value);
    }
  }
}
