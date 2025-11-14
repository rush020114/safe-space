package com.safespace.content_filter_backend.config;

import com.safespace.content_filter_backend.auth.filter.JwtConfirmFilter;
import com.safespace.content_filter_backend.auth.filter.LoginFilter;
import com.safespace.content_filter_backend.auth.util.JwtUtil;
import com.safespace.content_filter_backend.infra.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// spring security 의존성 주입으로 api호출이 되지 않는다.
// 인증받지 않은 사용자를 막고 있기 때문에
// 설정 파일을 만들어 각 api마다 접근할 수 있는 인증/인가에 대한 설정이 필요하다.
@Configuration
@EnableWebSecurity // security 기본 설정 활성화(보안 설정 컨트롤 가능)
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
  private final JwtUtil jwtUtil;
  private final RedisService redisService;
  // spring security에서 http 요청에 대한 보안 설정을 정의하는 메서드
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationConfiguration authenticationConfiguration) throws Exception {
    // 로그인 검증을 처리하는 객체를 의존성 주입으로 받아옴.
    AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

    httpSecurity
            // cors설정. 아래 corsConfigurationSource() 메서드에서 정의한 Bean을 등록.
            .cors(Customizer.withDefaults())
            // 세션을 사용하지 않는 JWT 기반 인증이므로 CSRF 보호는 불필요
            .csrf(csrf -> csrf.disable())
            // form로그인 방식을 쓰지 않고 api 처리 방식이기 떄문에 설정 필요 없음.
            .formLogin(form -> form.disable())
            // 요청마다 헤더에 로그인 정보를 담는 게 아닌 헤더에 토큰을 담으므로 불필요
            .httpBasic(basic -> basic.disable())
            // session을 STATELESS로 지정
            // JWT 방식 사용을 위해 설정(서버가 상태를 기억하지 않음)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 인증 및 인가에 대한 접근 설정
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers(HttpMethod.POST ,"/posts").authenticated() // 인증 정보를 가지고 있는 사람만 접근 가능
                        .requestMatchers(HttpMethod.POST, "/comments").authenticated()
                        .requestMatchers(HttpMethod.POST, "/reports").authenticated()
                        .requestMatchers("/admin/reports/stream").permitAll() // EventSource는 header로 요청 불가이므로 경로만 열기
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN") // ADMIN 권한만 접근 가능
                        .requestMatchers("/test3").hasAnyRole("MANAGER, ADMIN") // MANAGER, ADMIN 접근 가능
                        .anyRequest().permitAll() // 위 요청을 제외한 나머지 요청 접근 가능
            );

    // 로그인 프로세스를 진행하는 loginFilter 클래스를 SecurityFilterChain에 추가
    // UsernamePasswordAuthenticationFilter 클래스 위치에 LoginFilter를 사용하겠다는 뜻
    httpSecurity.addFilterAt(new LoginFilter(authenticationManager, jwtUtil, redisService), UsernamePasswordAuthenticationFilter.class);
    // JWT 토큰 존재 여부 및 유효성 검사를 위한 필터 추가
    // LoginFilter보다 먼저 실행되도록 필터 체인에 등록
    httpSecurity.addFilterBefore(new JwtConfirmFilter(jwtUtil, redisService), LoginFilter.class);

    return httpSecurity.build();
  }

  // cors 설정
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();

    // 인증 정보를 포함한 요청(cookie, JWT 등)을 허용
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.addAllowedOrigin("http://localhost:5173"); // react 요청 허용
    corsConfiguration.addAllowedHeader("*"); // 모든 헤더 정보 허용
    corsConfiguration.addAllowedMethod("*"); // get, post, delete, put 등의 요청 허용

    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    return urlBasedCorsConfigurationSource;
  }

  @Bean
  public PasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
