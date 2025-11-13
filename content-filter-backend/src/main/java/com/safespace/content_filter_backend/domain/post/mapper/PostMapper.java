package com.safespace.content_filter_backend.domain.post.mapper;

import com.safespace.content_filter_backend.domain.post.dto.PostDTO;
import com.safespace.content_filter_backend.domain.post.dto.PostImgDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
  // 게시글 등록
  void regPost(PostDTO postDTO);

  // 게시글 이미지 등록
  void regPostImg(PostImgDTO postImgDTO);

  // 게시글 번호 조회
  int getPostId();

  // 게시글 목록 조회
  List<PostDTO> getPostList();

  // 게시글 상세 조회
  PostDTO getPostDetail(int postId);

  // 게시글 필터링
  void filterPost(int postId);
}
