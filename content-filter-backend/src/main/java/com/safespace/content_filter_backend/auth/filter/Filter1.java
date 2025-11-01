package com.safespace.content_filter_backend.auth.filter;

import jakarta.servlet.*;

import java.io.IOException;

// 예외처리를 하면 예외가 발생해도 프로그램이 정상적으로 돌아갈 수 있고
// 사용자에게 올바른 방향을 알려줄 수 있다.
// throws는 해당 예외가 발생할 수도 있다는 것을 표현하는 키워드
// IOException : 데이터를 읽거나 쓰는 도중 문제가 생겼다. (요청 전과 요청 중)
// ServletException : 요청이 잘못됐거나 서버가 처리하면 안 되는 상황 (요청 다 받은 후 내용 검사)
// Servlet : 서버에서 요청을 처리하는 프로그램
public class Filter1 implements Filter {
  public void doFilter(ServletRequest servletRequest
          , ServletResponse servletResponse
          , FilterChain filterChain) throws IOException, ServletException {
    System.out.println("Filter1 실행");
    // 다음 필터 또는 서블릿으로 요청과 응답을 넘기는 과정
    // doFilter메서드 안에 doFilter가 선언될 수 있는 이유는
    // config파일의 setOrder 덕분
    // setOrder의 숫자가 바닥나면 서블릿으로 이동
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
