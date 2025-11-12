package com.safespace.content_filter_backend.comment.controller;

import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.comment.dto.CommentDTO;
import com.safespace.content_filter_backend.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("comments")
public class CommentController {
  private final CommentService commentService;
  private final JwtUtil jwtUtil;

  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> regComment(
          @RequestBody CommentDTO commentDTO
          , @RequestHeader("Authorization") String token
  ){
    try {
      String jwt = token.replace("Bearer ", "");
      log.info("comment jwt 토큰 : {}", jwt);
      commentDTO.setMemId(jwtUtil.getMemIdFromToken(jwt));
      commentService.regComment(commentDTO);
      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body("댓글 등록 성공");
    } catch (RuntimeException e){
      log.info("댓글 등록 실패 - 입력 오류 : {}", e.getMessage());
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(e.getMessage());
    } catch (Exception e){
      log.info("댓글 등록 실패 - 서버 오류", e);
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(e.getMessage());
    }
  }

}
