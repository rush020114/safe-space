package com.safespace.content_filter_backend.post.controller;

import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.post.dto.PostDTO;
import com.safespace.content_filter_backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;
  private final JwtUtil jwtUtil;

  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> regPost(@RequestBody PostDTO postDTO, @RequestHeader("Authorization") String token){
    try {
      // 토큰으로 memId 추출
      String jwt = token.replace("Bearer ", "");
      System.out.println("jwt 토큰 : " + jwt);
      postDTO.setMemId(jwtUtil.getMemIdFromToken(jwt));
      // 게시글 등록
      postService.regPost(postDTO);
      log.info("게시글 등록 성공");
      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body("게시글이 등록되었습니다.");
    } catch (RuntimeException e) {
      log.info("게시글 등록 실패 - 입력 오류 : {}", e.getMessage());
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(e.getMessage());
    } catch (Exception e) {
      log.info("회원가입 실패 - 서버 오류", e);
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("게시글 등록 중 서버 오류 발생");
    }
  }
}
