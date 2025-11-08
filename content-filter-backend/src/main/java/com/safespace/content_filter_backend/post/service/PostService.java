package com.safespace.content_filter_backend.post.service;

import com.safespace.content_filter_backend.post.dto.PostDTO;
import com.safespace.content_filter_backend.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
  public PostMapper postMapper;

  // 게시글 등록
  @Transactional(rollbackFor = Exception.class)
  public void regPost(PostDTO postDTO){
    String title = postDTO.getPostTitle();
    String content = postDTO.getPostContent();

    if(title == null || title.trim().isEmpty())
      throw new RuntimeException("제목을 입력해주세요.");

    if(content == null || content.trim().isEmpty())
      throw new RuntimeException("내용을 입력해주세요");

    // 게시글 번호 조회
    int postId = postMapper.getPostId();
    postDTO.setPostId(postId);

    // 게시글 등록
    postMapper.regPost(postDTO);
  }
}
