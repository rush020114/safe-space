package com.safespace.content_filter_backend.auth.dto;

import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 로그인으로 조회한 정보를 포장하기 위한 클래스
// Spring Security는 인증을 처리할 때 내부적으로 UserDetails 타입의 객체를 요구
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
  private final MemberDTO memberDTO;

  public MemberDTO getMemberDTO(){
    return memberDTO;
  }

  @Override // 계정의 권한 정보를 리턴
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collection = new ArrayList<>();

    collection.add(new GrantedAuthority() {
      @Override // 계정의 권한 정보를 리턴
      public String getAuthority() {
        return memberDTO.getMemRole();
      }
    });

    return collection;
  }

  @Override // 계정의 비밀번호를 리턴
  public String getPassword() {
    return memberDTO.getMemPw();
  }

  @Override // 계정의 아이디를 리턴
  public String getUsername() {
    return memberDTO.getMemEmail();
  }

  public int getUserId(){
    return memberDTO.getMemId();
  }

  @Override // 만료되지 않은 계정인가?
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override // 잠기지 않은 계정인가?
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override // 자격증명이 만료되지 않았는가?
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override // 사용가능한 상태의 계정인가?
  public boolean isEnabled() {
    return true;
  }

}
