package com.safespace.content_filter_backend.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// component : 직접 만든 클래스를 bean에 등록할 때 사용
@Component
public class JwtUtil {
  private SecretKey secretKey;

  // application.properties의 시크릿 키를 매개변수로 가져온다.
  public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS512.key().build().getAlgorithm());
  }

  // 공통 Claims 추출 메서드. token의 payload를 추출한다.
  private Claims parseClaims(String token) {
    return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  // token의 subject 추출 메서드. subject를 추출하면 유저이름(이메일)이 나온다.
  public String getUsername(String token){
    return parseClaims(token).getSubject();
  }

  // token의 권한 정보 추출
  public String getRole(String token){
    return parseClaims(token).get("role", String.class);
  }

  // memId 추출
  public Integer getMemIdFromToken(String token) {
    return parseClaims(token).get("memId", Integer.class);
  }

  // token의 만료시간이 지났으면 true
  public boolean isExpired(String token){
    try {
      return parseClaims(token).getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }

  // 토큰 생성 메서드
  public String createJwt(String username, String role, int memId, long expirationTime){
    return Jwts.builder()
            .signWith(secretKey, Jwts.SIG.HS512) // 암호화 방식 지정. 비밀키 & HS512 알고리즘으로 토큰 암호화 진행
            .header()
              .add("typ", "JWT")
              .add("alg", "HS512")
            .and()
              .subject(username)
              .claim("role", role)
              .claim("memId", memId)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expirationTime))
            .compact();
  }
}
