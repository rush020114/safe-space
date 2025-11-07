package com.safespace.content_filter_backend.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.member.dto.MemberDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

// UsernamePasswordAuthenticationFilter 클래스는 Spring Security에서 로그인 기능을 담당하는 클래스
// UsernamePasswordAuthenticationFilter를 상속받아 진행 방식을 커스터마이징한다.
// 객체 생성 어노테이션은 SecurityFilterChain에서 직접 해야 하기 때문에 불필요하다.
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
  // Spring Security에서 로그인 검증을 담당하는 핵심 인터페이스
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  // 생성자를 통해 authenticationManager 객체를 의존성 주입 받는다.
  public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil){
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;

    // 로그인 요청의 기본값과 아이디, 비밀번호의 기본값을 바꾸기 위한 세팅
    // 로그인 요청 url 설정
    setFilterProcessesUrl("/member/login");
    // 전달되는 아이디, 비번 key값 설정
    setUsernameParameter("memEmail");
    setPasswordParameter("memPw");
  }

  // 로그인 요청이 오면 입력받은 아이디, 비밀번호를 받음.
  @Override
  public Authentication attemptAuthentication (HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    log.info("attemptAuthentication method run");
    // memEmail과 memPw를 받아, MemberDTO 클래스의 객체인 vo에 저장하는 코드
    MemberDTO vo = new MemberDTO();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      ServletInputStream inputStream = request.getInputStream();
      String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
      vo = objectMapper.readValue(messageBody, MemberDTO.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("입력받은 아이디 : " + vo.getMemEmail());
    log.info("입력받은 비밀번호 : " + vo.getMemPw());

    // 우리가 입력한 아이디와 비밀번호가 데이터베이스에 저장된 정보와 일치하는지 검증하는 로직은
    // AuthenticationManger가 담당하기 때문에 전달받은 아이디와 비밀번호를 그 곳에 전달해줘야 한다.
    // 이때 아이디 비밀번호를 객체화하여 보내야 한다.
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(vo.getMemEmail(), vo.getMemPw(), null);

    // 로그인 검증하는 방법 -> UserDetailService의 loadUserByUsername() 메서드를 호출하여 검증
    // loadUserByUsername() 메서드 실행 결과로 로그인 유저의 정보를 authenticate에 객체로 담아온 후 manager가 검증.
    Authentication authentication = authenticationManager.authenticate(authenticationToken);

    log.info("DB에서 로그인 가능 여부 확인 완료(UserDetailService에 loadUserByUsername 메서드 정상 실행됨) 만약 검증에 실패했다면 본 출력문은 실행 안 됨.");
    log.info("로그인 중인 유저 : " + authentication.getName());

    // 로그인 유저의 정보가 담긴 authentication 객체를 리턴하면 그 객체는 session에 저장됨.
    // session에 저장되어야 security가 권한 처리를 할 수 있음.
    return authentication;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    log.info("로그인 검증 성공 - successfulAuthentication 메서드 실행");

    // 토큰 생성을 위한 아이디 정보 추출
    String username = authResult.getName();

    System.out.println("username : " + username);

    // 토큰 생성을 위한 권한 정보 추출
    Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    // 토큰 생성
    String accessToken = jwtUtil.createJwt(username, role, (1000 * 60 * 10));

    // 생성한 토큰을 응답 헤더에 담아 클라이언트에 전달
    // Authorization 헤더는 클라이언트가 접근하면 안 되므로 기본적으로 정보를 숨기는데 이를 명시적으로 보여주기 위한 설정 코드이다.
    response.setHeader("Access-Control-Expose-Headers", "Authorization");
    // jwt 토큰을 추가할 때 일반적으로 "Bearer " 키워드가 붙음.
    response.setHeader("Authorization", "Bearer " + accessToken);
    response.setStatus(HttpStatus.OK.value());
  }

  // 아이디는 맞지만 비밀번호가 틀렸을 때 실행
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
    log.info("로그인 검증 실패 - unsuccessfulAuthentication 메서드 실행");
    response.setStatus(401);
  }

}
