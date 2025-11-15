package com.safespace.content_filter_backend.domain.member.dto;

import com.safespace.content_filter_backend.domain.sanction.dto.SanctionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Schema(description = "회원 정보 DTO")
public class MemberDTO {

  @Schema(description = "회원 고유 ID", example = "1")
  private int memId;

  @Schema(description = "회원 이메일", example = "user@example.com")
  private String memEmail;

  @Schema(description = "회원 비밀번호", example = "securePassword123")
  private String memPw;

  @Schema(description = "회원 이름", example = "홍길동")
  private String memName;

  @Schema(description = "회원 역할 (USER 또는 ADMIN)", example = "USER")
  private String memRole;

  @Schema(description = "가입일시", example = "2025-11-15T10:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "회원 상태 (ACTIVE: 정상, WARNING: 경고, BANNED: 정지)", example = "ACTIVE")
  private String memStatus;

  @Schema(description = "경고 횟수", example = "2")
  private int warningCnt;

  @Schema(description = "정지 만료일 (정지 상태일 경우)", example = "2025-12-01T00:00:00")
  private LocalDateTime bannedUntil;

  @Schema(description = "회원 제재 정보 DTO")
  private SanctionDTO sanctionDTO;

  public MemberDTO(){
  }
}