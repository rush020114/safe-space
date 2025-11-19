package com.safespace.content_filter_backend.domain.post.controller;

import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.domain.post.dto.PostDTO;
import com.safespace.content_filter_backend.domain.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "게시글 관리", description = "게시글 등록, 조회 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
  private final PostService postService;
  private final JwtUtil jwtUtil;

  @Operation(
          summary = "게시글 등록",
          description = "이미지 파일과 게시글 정보를 함께 등록합니다. JWT 토큰에서 회원 ID를 추출하여 게시글 작성자로 설정합니다."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "게시글 등록 성공"),
          @ApiResponse(responseCode = "400", description = "입력 오류"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> regPost(
          @Parameter(description = "게시글 이미지 파일 (선택)")
          @RequestParam(name = "postImg", required = false) MultipartFile postImg,

          @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게시글 정보 DTO")
          PostDTO postDTO,

          @Parameter(description = "JWT 인증 토큰", example = "Bearer ...")
          @RequestHeader("Authorization") String token
  ){
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
  }

  // 게시글 목록 조회
  @Operation(summary = "게시글 목록 조회", description = "전체 게시글 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
  @GetMapping("")
  public ResponseEntity<?> getPostList(){
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(postService.getPostList());
  }

  // 게시글 상세 조회
  @Operation(summary = "게시글 상세 조회", description = "게시글 ID를 기반으로 상세 정보를 조회합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @Parameter(name = "postId", description = "게시글 ID", example = "10")
  @GetMapping("/{postId}")
  public ResponseEntity<?> getPostDetail(@PathVariable("postId") int postId) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(postService.getPostDetail(postId));
  }
}
