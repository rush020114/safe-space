package com.safespace.content_filter_backend.member.mapper;

import com.safespace.content_filter_backend.member.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
  // 로그인하려는 회원 정보 조회
  MemberDTO getMemberForLogin(String memEmail);
}
