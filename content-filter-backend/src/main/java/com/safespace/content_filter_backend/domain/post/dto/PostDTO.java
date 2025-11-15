package com.safespace.content_filter_backend.domain.post.dto;

import com.safespace.content_filter_backend.domain.comment.dto.CommentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Schema(description = "게시글 DTO")
public class PostDTO {

  @Schema(description = "게시글 ID", example = "101")
  private int postId;

  @Schema(description = "게시글 제목", example = "욕설 필터링 테스트")
  private String postTitle;

  @Schema(description = "게시글 내용", example = "이 게시글은 필터링 테스트용입니다.")
  private String postContent;

  @Schema(description = "욕설 필터링 여부", example = "N", allowableValues = {"Y", "N"})
  private String isFiltered;

  @Schema(description = "게시글 작성일시", example = "2025-11-15T11:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "작성자 회원 ID", example = "2001")
  private int memId;

  @Schema(description = "게시글에 첨부된 이미지 정보")
  private PostImgDTO postImgDTO;

  @Schema(description = "게시글에 달린 댓글 목록")
  private List<CommentDTO> commentDTOList;

  public PostDTO() {
  }
}