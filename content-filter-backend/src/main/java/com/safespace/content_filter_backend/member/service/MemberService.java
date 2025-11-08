package com.safespace.content_filter_backend.member.service;

import com.safespace.content_filter_backend.member.dto.MemberDTO;

public interface MemberService {
  // 로그인하려는 회원 정보 조회
  MemberDTO getMemberForLogin(String memEmail);

  // 회원 가입
  void regMember(MemberDTO memberDTO);
}
