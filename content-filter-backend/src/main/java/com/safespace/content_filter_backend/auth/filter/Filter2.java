package com.safespace.content_filter_backend.auth.filter;

import jakarta.servlet.*;

import java.io.IOException;

public class Filter2 implements Filter {
  public void doFilter(ServletRequest servletRequest
          , ServletResponse servletResponse
          , FilterChain filterChain) throws IOException, ServletException {
    System.out.println("filter2 실행");
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
