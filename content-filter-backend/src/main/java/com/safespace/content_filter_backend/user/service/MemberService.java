package com.safespace.content_filter_backend.user.service;

import com.safespace.content_filter_backend.user.dto.MemberDTO;

public interface MemberService {
  // 로그인하려는 회원 정보 조회
  MemberDTO getMemberForLogin(String memEmail);
}
