package com.safespace.content_filter_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private int port;

  @Value("${spring.redis.password}")
  private String password;

  // Redis 서버 연결 정보를 설정하고, Lettuce 클라이언트를 통해 연결 팩토리 생성
  @Bean
  public RedisConnectionFactory redisConnectionFactory(){
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(host);
    redisStandaloneConfiguration.setPort(port);
    redisStandaloneConfiguration.setPassword(password);
    // LettuceConnectionFactory: Redis와 연결을 관리하는 클라이언트
    // Spring에서 Redis를 쓸 때는, 내부에 등록된 Redis 클라이언트(Lettuce)가 Redis 서버로 요청을 보내는 구조
    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }

  // Redis에 데이터를 넣고 꺼낼 수 있는 도구(RedisTemplate)를 Spring에 등록해주는 역할
  @Bean
  public RedisTemplate<String, String> redisTemplate(){
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    // Redis에 데이터를 넣고 꺼낼 수 있는 도구에 서버 연결 설정을 포함
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    // Redis에 저장할 때 key/value를 어떻게 변환할지 지정
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    return redisTemplate;
  }
}
