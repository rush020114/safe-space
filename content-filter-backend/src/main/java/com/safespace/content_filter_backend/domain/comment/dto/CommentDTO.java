package com.safespace.content_filter_backend.domain.comment.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDTO {
  private int cmtId;             // 댓글 번호
  private String cmtContent;     // 댓글 내용
  private LocalDateTime createdAt; // 작성일
  private String isFiltered;     // 필터링 여부 (Y/N)
  private int postId;            // 게시글 번호
  private int memId;             // 회원 번호

  public CommentDTO() {
  }
}