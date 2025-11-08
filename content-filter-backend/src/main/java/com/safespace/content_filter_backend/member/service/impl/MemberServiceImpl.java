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

    String email = memberDTO.getMemEmail();
    String pw = memberDTO.getMemPw();
    String name = memberDTO.getMemName();

    // 이메일 빈값 체크
    if (email == null || email.trim().isEmpty())
      throw new RuntimeException("아이디를 입력해주세요.");

    // 이메일 중복 여부 체크
    if (memberMapper.countByEmail(email.trim()) > 0)
      throw new RuntimeException("이미 존재하는 이메일입니다.");

    // 비밀번호 빈값 체크
    if (pw == null || pw.trim().isEmpty())
      throw new RuntimeException("비밀번호를 입력해주세요");

    // 비밀번호 최소 자리수 체크
    if (pw.length() < 4)
      throw new RuntimeException("비밀번호는 최소 4자리 이상이어야 합니다.");

    // 이름 빈값 체크
    if (name == null || name.trim().isEmpty())
      throw new RuntimeException("이름을 입력해주세요");

    // 비밀번호 암호화
    String encodePassword = passwordEncoder.encode(pw);
    memberDTO.setMemPw(encodePassword);

    memberMapper.regMember(memberDTO);
  }
}
