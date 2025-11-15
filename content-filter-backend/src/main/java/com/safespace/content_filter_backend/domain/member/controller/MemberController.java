package com.safespace.content_filter_backend.domain.member.controller;

import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import com.safespace.content_filter_backend.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 관리", description = "회원가입 및 회원 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
  private final MemberService memberService;
  private final JwtUtil jwtUtil;

  @Operation(summary = "회원가입", description = "사용자가 이메일, 비밀번호 등으로 회원가입을 진행합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "회원가입 성공"),
          @ApiResponse(responseCode = "400", description = "입력 오류"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @PostMapping("")
  public ResponseEntity<?> regMember(
          @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원가입 정보")
          @RequestBody MemberDTO memberDTO
  ){
    try {
      memberService.regMember(memberDTO);
      log.info("회원가입 성공 - 이메일 : {}", memberDTO.getMemEmail());
      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body("회원가입 성공");
    } catch (RuntimeException e) {
      log.info("회원가입 실패 - 입력 오류 : {}", e.getMessage());
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(e.getMessage());
    } catch (Exception e) {
      log.info("회원가입 실패 - 서버 오류", e);
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("회원가입 중 서버 오류 발생");
    }
  }

}
