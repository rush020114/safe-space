package com.safespace.content_filter_backend.domain.member.mapper;

import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
  void banMember(@Param("bannedUntil") String bannedUntil, int reportedId);

  // 회원 제재를 위한 조회
  MemberDTO getMemberStatusById(int memId);

  // 정지 해제 대상 조회 (BANNED이고 bannedUntil이 현재 시간 이전)
  List<MemberDTO> getExpiredBannedMembers(@Param("now") LocalDateTime now);

  // 회원 상태 업데이트
  void updateMemberStatus(@Param("memId") int memId, @Param("status") String status);
}
