package com.safespace.content_filter_backend.report.model;

public enum ReportTargetType {
  POST
  , COMMENT;

  public static ReportTargetType from(String value){
    try {
      return ReportTargetType.valueOf(value);
    } catch (Exception e){
      throw new IllegalArgumentException("지원하지 않는 신고 유형입니다 : " + value);
    }
  }
}
