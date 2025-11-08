package com.safespace.content_filter_backend.post.service;

import com.safespace.content_filter_backend.profanity.filter.ProfanityFilter;
import com.safespace.content_filter_backend.post.dto.PostDTO;
import com.safespace.content_filter_backend.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
  public final PostMapper postMapper;
  public final ProfanityFilter profanityFilter;

  // 게시글 등록
  @Transactional(rollbackFor = Exception.class)
  public void regPost(PostDTO postDTO){
    String title = postDTO.getPostTitle();
    String content = postDTO.getPostContent();

    if(title == null || title.trim().isEmpty())
      throw new RuntimeException("제목을 입력해주세요.");

    if(content == null || content.trim().isEmpty())
      throw new RuntimeException("내용을 입력해주세요");

    // 욕설 판단
    if (profanityFilter.containsProfanity(title))
      throw new RuntimeException("제목에 부적절한 단어가 포함되어 있습니다.");

    if(profanityFilter.containsProfanity(content)){
      throw new RuntimeException("내용에 부적절한 단어가 포함되어 있습니다.");
    }

    // 게시글 번호 조회
    int postId = postMapper.getPostId();
    postDTO.setPostId(postId);

    log.info("postDTO의 값 : {}", postDTO);
    // 게시글 등록
    postMapper.regPost(postDTO);
  }
}
