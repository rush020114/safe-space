package com.safespace.content_filter_backend.domain.comment.controller;

import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.domain.comment.dto.CommentDTO;
import com.safespace.content_filter_backend.domain.comment.service.CommentService;
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

@Tag(name = "댓글 API", description = "댓글 등록 및 관련 기능")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("comments")
public class CommentController {
  private final CommentService commentService;
  private final JwtUtil jwtUtil;

  @Operation(
          summary = "댓글 등록",
          description = "회원이 댓글을 등록합니다. JWT 토큰을 헤더에 포함해야 하며, 댓글 내용은 요청 바디로 전달됩니다."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "댓글 등록 성공"),
          @ApiResponse(responseCode = "400", description = "입력 오류"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> regComment(
          @RequestBody CommentDTO commentDTO,
          @Parameter(description = "JWT 토큰 (Bearer {token})", example = "Bearer ...")
          @RequestHeader("Authorization") String token
  ){
    String jwt = token.replace("Bearer ", "");
    log.info("comment jwt 토큰 : {}", jwt);
    commentDTO.setMemId(jwtUtil.getMemIdFromToken(jwt));
    commentService.regComment(commentDTO);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("댓글 등록 성공");
  }

}
