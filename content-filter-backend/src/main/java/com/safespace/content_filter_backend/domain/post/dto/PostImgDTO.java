package com.safespace.content_filter_backend.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "게시글 이미지 DTO")
public class PostImgDTO {

  @Schema(description = "이미지 고유 번호", example = "301")
  private int imgNum;

  @Schema(description = "원본 이미지 파일명", example = "original_image.jpg")
  private String originImgName;

  @Schema(description = "서버에 저장된 이미지 파일명", example = "20251115_113000_abc123.jpg")
  private String attachedImgName;

  @Schema(description = "이미지가 속한 게시글 ID", example = "101")
  private int postId;

  public PostImgDTO(){
  }
}
