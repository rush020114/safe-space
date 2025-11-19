package com.safespace.content_filter_backend.domain.sanction.service;

import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import com.safespace.content_filter_backend.domain.member.mapper.MemberMapper;
import com.safespace.content_filter_backend.domain.sanction.dto.SanctionDTO;
import com.safespace.content_filter_backend.domain.sanction.mapper.SanctionMapper;
import com.safespace.content_filter_backend.infra.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SanctionService {
  private final MemberMapper memberMapper;
  private final SanctionMapper sanctionMapper;
  private final RedisService redisService;

  // propagation : 트랜잭션과 함께 실행되면 어떻게 할래?
  // MANDATORY: ReportService의 트랜잭션에 참여
  @Transactional(propagation = Propagation.MANDATORY)
  public void applySanctionIfNeeded(int memId, int adminId){
    // 현재 경고 횟수 조회
    int warningCnt = memberMapper.getMemberStatusById(memId).getWarningCnt();

    // 경고 횟수에 따라 제재 적용
    if (warningCnt >= 9) {
      applyPermanentBan(memId, adminId);
    } else if (warningCnt == 6) {
      applyTempBan(memId, adminId, 3, "BAN_TEMP_2");
    } else if (warningCnt == 3) {
      applyTempBan(memId, adminId, 1, "BAN_TEMP_1");
    } else {
      updateWarningOnly(memId);
    }
  }

  // 영구 정지
  private void applyPermanentBan(int memId, int adminId) {
    LocalDateTime bannedUntil = LocalDateTime.of(2099, 12, 31, 23, 59);
    String reason = "신고 9회 누적으로 영구 정지";

    banMember(memId, adminId, "BAN_PERMANENT", reason, bannedUntil, null);
  }

  // 임시 정지
  private void applyTempBan(int memId, int adminId, int minutes, String sanctionType) {
    LocalDateTime bannedUntil = LocalDateTime.now().plusMinutes(minutes);
    String reason = String.format("신고 누적으로 %s까지 정지", bannedUntil);

    banMember(memId, adminId, sanctionType, reason, bannedUntil, bannedUntil);
  }

  // 정지 처리 공통 로직
  private void banMember(int memId, int adminId, String sanctionType, String reason,
                         LocalDateTime bannedUntil, LocalDateTime endDate) {

    // 1. 제재 기록 저장
    SanctionDTO sanctionDTO = new SanctionDTO();
    sanctionDTO.setMemId(memId);
    sanctionDTO.setAdminId(adminId);
    sanctionDTO.setSanctionType(sanctionType);
    sanctionDTO.setSanctionReason(reason);
    sanctionDTO.setEndDate(endDate);
    sanctionMapper.regSanction(sanctionDTO);

    // 2. 회원 정지
    String bannedUntilStr = bannedUntil.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    memberMapper.banMember(bannedUntilStr, memId);

    // 3. Redis 업데이트 (JwtConfirmFilter에서 즉시 차단!)
    redisService.updateMemberStatus(memId, "BANNED", bannedUntil, sanctionType, reason);
  }

  // 제재 없이 경고만
  private void updateWarningOnly(int memId) {
    log.info("경고 횟수만 업데이트 - 제재 없음");
    MemberDTO updated = memberMapper.getMemberStatusById(memId);
    redisService.cacheMemberSanctionInfo(memId, updated);
  }
}
