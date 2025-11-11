package com.safespace.content_filter_backend.post.dto;

import com.safespace.content_filter_backend.comment.dto.CommentDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

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
  private List<CommentDTO> commentDTOList;

  public PostDTO() {
  }
}