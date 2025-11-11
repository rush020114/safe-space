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
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
  private final PostService postService;
  private final JwtUtil jwtUtil;

  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> regPost(
          @RequestParam (name = "postImg", required = false) MultipartFile postImg
          , PostDTO postDTO
          , @RequestHeader("Authorization") String token
  ){
    try {
      log.info("이미지 파일 : {}", postImg);
      // 토큰으로 memId 추출
      String jwt = token.replace("Bearer ", "");
      log.info("post jwt 토큰 : {}", jwt);
      postDTO.setMemId(jwtUtil.getMemIdFromToken(jwt));
      // 게시글 등록
      postService.regPost(postImg, postDTO);
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
      log.info("게시글 등록 실패 - 서버 오류", e);
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("게시글 등록 중 서버 오류 발생");
    }
  }

  // 게시글 목록 조회
  @GetMapping("")
  public ResponseEntity<?> getPostList(){
    try {
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(postService.getPostList());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(e.getMessage());
    }
  }

  // 게시글 상세 조회
  @GetMapping("/{postId}")
  public ResponseEntity<?> getPostDetail(@PathVariable("postId") int postId){
    try{
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(postService.getPostDetail(postId));
    } catch (Exception e){
      e.printStackTrace();
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(e.getMessage());
    }
  }
}
