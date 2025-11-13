package com.safespace.content_filter_backend.infra.redis;

import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import com.safespace.content_filter_backend.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// Redis는 “빠르고 실시간으로 바뀌는 데이터”를 다루기 위한 무기
// DB는 느리고, JWT는 상태 반영을 토큰 만료까지 기다려야 하니 그 사이를 Redis가 메꿈.
// JWT는 토큰 만료까지 기다려야 하므로 실시간 반영이 불가능
@Service
@RequiredArgsConstructor
public class RedisService {
  // Redis랑 통신할 때 쓰는 도구 (Spring에서 제공)
  // 설정 파일로 인해 Redis서버와 연결, key와 value가 문자열로 저장될 수 있게 됨.
  private final RedisTemplate<String, String> redisTemplate;
  private final MemberMapper memberMapper;

  // Redis에서 회원 상태 조회 후 캐싱(캐싱 : Redis에 DB에서 조회한 데이터를 저장 - 앞으로 빠르게 찾기 위해)
  public String getMemberStatus(int memId) {
    // 캐시에서 값 꺼내오기
    String key = "member:" + memId + ":status";
    String cachedStatus = redisTemplate.opsForValue().get(key);

    if(cachedStatus != null){
      // 캐시 히트 : 필요한 데이터를 DB나 원본 저장소까지 가지 않고, Redis 같은 캐시에서 바로 찾은 경우
      return cachedStatus;
    }

    // 캐시 미스 -> DB 조회
    MemberDTO memberDTO = memberMapper.getMemberStatusById(memId);
    if(memberDTO != null){
      String status = memberDTO.getMemStatus();
      // Redis에 24시간 캐싱
      // 1. update 누락이 없다면
      // 상태 변경 시 updateMemberStatus()가 항상 호출됨
      // Redis 값은 즉시 갱신되니까 TTL이 길어도 문제 없음
      // 2. 제재 상태는 민감하지 않다
      // 보안상 노출돼도 큰 문제가 없는 정보
      // Redis에 오래 저장해도 괜찮음
      // 3. 조회 성능 최적화
      // 24시간 동안 DB 접근 없이 Redis에서 바로 조회
      // 캐시 히트율 높아지고 서버 부하 줄어듦
      redisTemplate.opsForValue().set(key, status, 24, TimeUnit.HOURS);
      return status;
    }

    return "ACTIVE";
  }

  // 제재 시 Redis 캐시 즉시 업데이트
  public void updateMemberStatus(int memId, String newStatus) {
    String key = "member:" + memId + ":status";
    redisTemplate.opsForValue().set(key, newStatus, 24, TimeUnit.HOURS);
  }
}
