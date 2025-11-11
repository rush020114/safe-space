package com.safespace.content_filter_backend.comment.mapper;

import com.safespace.content_filter_backend.comment.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {
  // 댓글 등록
  void regComment(CommentDTO commentDTO);
}
