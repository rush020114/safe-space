package com.safespace.content_filter_backend.util;

public enum UploadPath {

  // 게시글 이미지 업로드 경로
  POST("post");

  private final String path;

  UploadPath(String path){
    this.path = path;
  }

  public String getPath(){
    return this.path;
  }

}
