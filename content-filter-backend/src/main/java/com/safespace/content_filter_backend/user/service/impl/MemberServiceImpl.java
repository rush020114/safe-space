package com.safespace.content_filter_backend.user.service.impl;

import com.safespace.content_filter_backend.user.dto.MemberDTO;
import com.safespace.content_filter_backend.user.mapper.MemberMapper;
import com.safespace.content_filter_backend.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 인터페이스를 커스터마이징하여 검증한다.
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  private final MemberMapper memberMapper;

  // 로그인 하려는 회원 정보 조회
  public MemberDTO getMemberForLogin(String memEmail){
    return memberMapper.getMemberForLogin(memEmail);
  }
}
