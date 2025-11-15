package com.safespace.content_filter_backend.domain.report.dto;

import com.safespace.content_filter_backend.domain.comment.dto.CommentDTO;
import com.safespace.content_filter_backend.domain.post.dto.PostDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Schema(description = "신고 정보 DTO")
public class ReportDTO {
  @Schema(description = "신고 ID", example = "101")
  private int reportId;

  @Schema(description = "신고 대상 유형", example = "POST")
  private String targetType;

  @Schema(description = "신고 대상 ID", example = "55")
  private int targetId;

  @Schema(description = "신고 사유", example = "욕설 및 비방")
  private String reportReason;

  @Schema(description = "신고 처리 상태", example = "PENDING")
  private String reportStatus;

  @Schema(description = "신고 생성일", example = "2025-11-15T10:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "신고자 회원 ID", example = "2001")
  private int reporterId;

  @Schema(description = "신고된 게시글 정보")
  private PostDTO postDTO;

  @Schema(description = "신고된 댓글 정보")
  private CommentDTO commentDTO;


  public ReportDTO() {
  }
}
