package com.safespace.content_filter_backend.domain.comment.mapper;

import com.safespace.content_filter_backend.domain.comment.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {
  // 댓글 등록
  void regComment(CommentDTO commentDTO);

  // 댓글 필터링
  void filterComment(int cmtId);
}
