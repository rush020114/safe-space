package com.safespace.content_filter_backend.member.controller;

import com.safespace.content_filter_backend.member.dto.MemberDTO;
import com.safespace.content_filter_backend.member.service.MemberService;
import com.safespace.content_filter_backend.member.service.impl.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
  private final MemberService memberService;

  @PostMapping("")
  public ResponseEntity<?> regMember(@RequestBody MemberDTO memberDTO){
    try {
      memberService.regMember(memberDTO);
      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body("회원가입 성공");
    } catch (RuntimeException e) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("회원가입 중 서버 오류 발생");
    }
  }

}
