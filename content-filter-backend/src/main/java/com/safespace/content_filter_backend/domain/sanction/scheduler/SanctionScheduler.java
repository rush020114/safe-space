package com.safespace.content_filter_backend.domain.sanction.scheduler;

import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import com.safespace.content_filter_backend.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component // Bean에 등록 후 @Scheduled 어노테이션이 붙은 메서드를 자동으로 주기적으로 실행
@RequiredArgsConstructor
public class SanctionScheduler {
  private final MemberMapper memberMapper;
  private final RedisTemplate<String, Object> redisTemplate;

  // 매분마다 만료된 정지 해제 DB 기준으로 조회 → Redis는 동기화만
  // cron에 있는 문자열은 매분 0초라는 뜻
  @Scheduled(cron = "0 * * * * *")
  public void releaseExpiredBans(){
    log.info("정지 해제 스케줄러 실행");

    LocalDateTime now = LocalDateTime.now();

    // DB에서 해제 대상 조회
    List<MemberDTO> expiredMembers = memberMapper.getExpiredBannedMembers(now);

    if(expiredMembers.isEmpty()){
      log.info("정지 해제 대상 없음");
      return;
    }

    int releasedCnt = 0;

    for(MemberDTO member : expiredMembers){
      try {
        int memId = member.getMemId();

        // DB 업데이트
        memberMapper.updateMemberStatus(memId, "ACTIVE");

        // Redis 동기화 (키가 있으면)
        String redisKey = "member:" + memId;
        Boolean hasKey = redisTemplate.hasKey(redisKey);

        if (hasKey != null && hasKey) {
          // Redis 캐시 있으면 업데이트
          Map<String, String> updates = Map.of(
                  "status", "ACTIVE",
                  "bannedUntil", ""
          );
          // redisTemplate.opsForHash().putAll(redisKey, updates);
          log.info("Redis 동기화 완료: memId={}", memId);
        } else {
          log.info("Redis 캐시 없음 (DB만 업데이트): memId={}", memId);
        }
        releasedCnt++;
        log.info("정지 해제: memId={}, bannedUntil={}",
                memId, member.getBannedUntil());
      } catch (Exception e) {
        log.error("정지 해제 처리 중 오류: memId={}", member.getMemId(), e);
      }
    }

    log.info("정지 해제 스케줄러 완료: {}명 해제", releasedCnt);
  }
}
