package com.safespace.content_filter_backend.domain.member.mapper;

import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
  // 로그인하려는 회원 정보 조회
  MemberDTO getMemberForLogin(String memEmail);

  // 회원 가입
  void regMember(MemberDTO memberDTO);

  // 이메일 중복 조회
  int countByEmail(String memEmail);

  // 회원 상태 수정
  void handleMemberStatus(int reportedId);

  // 회원 제재를 위한 조회(redis)
  MemberDTO getMemberStatusById(int memId);
}
