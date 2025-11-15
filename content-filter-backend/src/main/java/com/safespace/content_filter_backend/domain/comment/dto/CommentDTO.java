package com.safespace.content_filter_backend.domain.comment.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Schema(description = "댓글 DTO")
public class CommentDTO {

  @Schema(description = "댓글 ID", example = "501")
  private int cmtId;

  @Schema(description = "댓글 내용", example = "이 게시글은 정말 유익하네요!")
  private String cmtContent;

  @Schema(description = "댓글 작성일시", example = "2025-11-15T12:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "욕설 필터링 여부", example = "Y", allowableValues = {"Y", "N"})
  private String isFiltered;

  @Schema(description = "댓글이 속한 게시글 ID", example = "101")
  private int postId;

  @Schema(description = "댓글 작성자 회원 ID", example = "2001")
  private int memId;

  public CommentDTO() {
  }
}