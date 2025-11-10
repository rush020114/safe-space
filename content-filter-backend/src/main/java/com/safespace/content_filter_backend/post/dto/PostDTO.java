package com.safespace.content_filter_backend.post.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class PostDTO {
  private int postId;
  private String postTitle;
  private String postContent;
  private String isFiltered;
  private LocalDateTime createdAt;
  private int memId;
  private PostImgDTO postImgDTO;

  public PostDTO() {
  }
}