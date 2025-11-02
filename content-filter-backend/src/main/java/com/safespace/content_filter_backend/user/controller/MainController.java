package com.safespace.content_filter_backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {
  // securityConfig에서 등록한 passwordEncoder Bean 의존성 주입
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/test1")
  public String test1(){
    String password = "1234";
    String encodePw = passwordEncoder.encode(password);
    // passwordEncoder.matches(원본문자열, 암호화된 문자열) -> 비교하여 같으면 true
    passwordEncoder.matches("1234", encodePw);
    passwordEncoder.matches("1111", encodePw);

    return "test1";
  }
}
