package com.safespace.content_filter_backend.report.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ReportDTO {
  private int reportId;           // 신고 아이디
  private String targetType;      // 신고 유형 (POST, CMT 등)
  private int targetId;           // 신고 대상 ID
  private String reportReason;    // 신고 사유
  private String reportStatus;    // 신고 상태
  private LocalDateTime createdAt; // 신고일
  private int reporterId;         // 신고자 ID

  public ReportDTO() {
  }
}
