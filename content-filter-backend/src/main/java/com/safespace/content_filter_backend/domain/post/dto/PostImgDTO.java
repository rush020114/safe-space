package com.safespace.content_filter_backend.domain.post.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostImgDTO {
  private int imgNum;
  private String originImgName;
  private String attachedImgName;
  private int postId;

  public PostImgDTO(){
  }
}
