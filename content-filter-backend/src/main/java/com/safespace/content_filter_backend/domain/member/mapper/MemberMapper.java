package com.safespace.content_filter_backend.domain.member.mapper;

import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface MemberMapper {
  // 로그인하려는 회원 정보 조회
  MemberDTO getMemberForLogin(String memEmail);

  // 회원 가입
  void regMember(MemberDTO memberDTO);

  // 이메일 중복 조회
  int countByEmail(String memEmail);

  // 회원 경고 횟수 증가
  void addWarningCnt(int reportedId);

  // 회원 제재
  void banMember(LocalDateTime bannedUntil, int reportedId);

  // 회원 제재를 위한 조회
  MemberDTO getMemberStatusById(int memId);
}
