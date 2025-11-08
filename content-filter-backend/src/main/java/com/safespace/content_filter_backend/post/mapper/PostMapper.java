package com.safespace.content_filter_backend.post.mapper;

import com.safespace.content_filter_backend.post.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {
  // 게시글 등록
  void regPost(PostDTO postDTO);

  // 게시글 번호 조회
  int getPostId();
}
