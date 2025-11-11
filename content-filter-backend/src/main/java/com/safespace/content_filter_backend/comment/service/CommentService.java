package com.safespace.content_filter_backend.comment.service;

import com.safespace.content_filter_backend.comment.dto.CommentDTO;
import com.safespace.content_filter_backend.comment.mapper.CommentMapper;
import com.safespace.content_filter_backend.content_filter.ProfanityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentMapper commentMapper;
  private final ProfanityFilter profanityFilter;

  // 댓글 등록
  public void regComment(CommentDTO commentDTO){
    String content = commentDTO.getCmtContent();

    // 댓글이 없다면
    if(content == null || content.isEmpty()){
      throw new RuntimeException("댓글을 입력해주세요.");
    }

    // 댓글 필터링
    if(profanityFilter.containsProfanity(content)){
      throw new RuntimeException("부적절한 단어가 포함되어 있습니다");
    }

    commentMapper.regComment(commentDTO);
  }
}
