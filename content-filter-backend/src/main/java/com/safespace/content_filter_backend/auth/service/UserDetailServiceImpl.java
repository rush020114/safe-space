package com.safespace.content_filter_backend.auth.service;

import com.safespace.content_filter_backend.auth.domain.CustomUserDetails;
import com.safespace.content_filter_backend.user.dto.MemberDTO;
import com.safespace.content_filter_backend.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// AuthenticationManager는 로그인 검증을 위해 UserDetailsService 인터페이스에 선언된 loadUserByUsername() 메서드를 호출한다.
// loadUserByUsername() 메서드에서는 로그인을 시도하는 유저의 정보를 데이터베이스에서 조회하여 return 시켜주면 된다.
// 그러면, AuthenticationManager는 조회한 로그인 정보를 받아, 내부적으로 로그인 검증을 시작한다.
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
  private final MemberService memberService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("loadUserByUsername 메서드 실행");
    // 인터페이스를 구현한 java파일이 있지만 interface를 사용해야 한다.
    // 인터페이스의 추상메서드를 호출하면 java파일이 실행된다.
    MemberDTO memberDTO = memberService.getMemberForLogin(username);

    if(memberDTO == null){
      log.info("==========일치하는 아이디 없음==========");
      // 401 상태코드 반환
      throw new UsernameNotFoundException("없는 아이디 : " + username);

    }
    return new CustomUserDetails(memberDTO);
  }

}
