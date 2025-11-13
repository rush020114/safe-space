package com.safespace.content_filter_backend.auth.filter;

import com.safespace.content_filter_backend.auth.dto.CustomUserDetails;
import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 로그인 검증에 성공하면 클라이언트에게 토큰을 발급한다.
// 해당 필터는 클라이언트가 들고올 토큰이 존재하는지 유효하는지를 판단한다.
// SecurityContext가 비어 있는 상태에서 헤더나 URL에서 토큰을 꺼내 인증 정보를 주입하는 역할
@Slf4j
@RequiredArgsConstructor
public class JwtConfirmFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    log.info("JwtConfirmFilter - doFilerInternal() 메서드 실행");

    // 인증 정보가 없으면
    if(SecurityContextHolder.getContext().getAuthentication() == null){
      String token = null;

      // 헤더에서 토큰 찾기
      String authorization = request.getHeader("Authorization");
      if(authorization != null && authorization.startsWith("Bearer ")){
        log.info("헤더의 토큰 추출 완료");
        token = authorization.split(" ")[1];
      }

      // SSE의 url에서 토큰 찾기
      if(token == null && request.getRequestURL().toString().contains("/admin/reports/stream")){
        String paramToken = request.getParameter("token");
        if(paramToken != null && paramToken.startsWith("Bearer ")){
          log.info("url의 토큰 추출 완료");
          token = paramToken.substring(7);
        }
      }

      // 3. 토큰 있고 유효하면 인증 정보 주입
      if (token != null && !jwtUtil.isExpired(token)) {
        log.info("토큰이 정상적으로 검증되었습니다.");
        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // userEntity를 생성하여 값 set
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setMemEmail(username);
        memberDTO.setMemRole(role);

        // userDetail에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(memberDTO);

        // spring security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자 저장, 일시적으로 세션에 사용자 정보를 저장하는 이유는 유저의 권한 체크 때문이다.
        SecurityContextHolder.getContext().setAuthentication(authToken);
      } else {
        log.warn("토큰이 인증되지 않았습니다. (없거나 만료됨)");
      }
      // filterChain.doFilter() 이후의 처리 흐름은 SecurityContext에 주입된 인증 정보에 따라 달라진다.
      // 따라서 인증이 완료된 경우 한 번만 doFilter를 호출하면 충분하다.
    }

    filterChain.doFilter(request, response);
  }
}
