package com.safespace.content_filter_backend.auth.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 해당 클래스의 객체 생성 + 설정 파일임을 의미
public class FilterConfig {

  // 내가 직접 만든 객체를 스프링이 대신 관리하게 해주는 방법
  // (이 메서드가 리턴하는 객체를 스프링 컨테이너에 등록해서 다른 데서도 꺼내 쓸 수 있게 해줘)
  // 리턴한 객체가 스프링 콘테이너에 등록됨으로써 요청에 대한 필터를 실행할 수 있다.
  @Bean
  public FilterRegistrationBean<Filter1> myFilterRegistration() {
    // FilterRegistrationBean : 필터 등록을 위한 클래스 (bean이 등록해주면 서블릿보다 먼저 실행 - 그래서 우리가 만든 필터가 실행)
    // <Filter1>을 제네릭으로 지정해줬기 때문에
    // setFilter에 new Filter1()을 매개변수로 넣는 게 가능
    FilterRegistrationBean<Filter1> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(new Filter1()); // 사용할 필터 지정
    registrationBean.addUrlPatterns("/*"); // 모든 요청에 대해 작동
    registrationBean.setOrder(0); // 숫자가 작을수록 먼저 실행됨. (현재는 filter을 0순위로 지정)

    return registrationBean; // 스프링이 관리할 객체로 등록(Bean에 의해서 관리가 되고
  }

  @Bean
  public FilterRegistrationBean<Filter2> myFilterRegistration2() {
    FilterRegistrationBean<Filter2> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(new Filter2());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(0);

    return registrationBean;
  }
}
