package com.safespace.content_filter_backend.member.service.impl;

import com.safespace.content_filter_backend.member.dto.MemberDTO;
import com.safespace.content_filter_backend.member.mapper.MemberMapper;
import com.safespace.content_filter_backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 인터페이스를 커스터마이징하여 검증한다.
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;

  // 로그인 하려는 회원 정보 조회
  public MemberDTO getMemberForLogin(String memEmail){
    return memberMapper.getMemberForLogin(memEmail);
  }

  // 회원가입
  @Transactional(rollbackFor = Exception.class)
  public void regMember(MemberDTO memberDTO){
    // 이메일 중복 체크
    if(memberMapper.countByEmail(memberDTO.getMemEmail().trim()) > 0){
      throw new RuntimeException("이미 존재하는 이메일입니다.");
    }

    // 비밀번호 암호화
    String encodePassword = passwordEncoder.encode(memberDTO.getMemPw());
    memberDTO.setMemPw(encodePassword);

    memberMapper.regMember(memberDTO);
  }
}
