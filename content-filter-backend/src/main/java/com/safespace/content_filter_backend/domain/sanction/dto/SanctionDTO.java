package com.safespace.content_filter_backend.domain.sanction.dto;

import com.safespace.content_filter_backend.domain.member.dto.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "제재 DTO")
public class SanctionDTO {

  @Schema(description = "제재 ID", example = "701")
  private int sanctionId;

  @Schema(description = "제재 유형", example = "BAN_TEMP", allowableValues = {"WARNING", "BAN_TEMP", "BAN_PERMANENT"})
  private String sanctionType;

  @Schema(description = "제재 사유", example = "욕설 누적 사용")
  private String sanctionReason;

  @Schema(description = "제재 시작일시", example = "2025-11-15T12:00:00")
  private LocalDateTime startDate;

  @Schema(description = "제재 종료일시 (영구 제재는 null)", example = "2025-11-22T12:00:00")
  private LocalDateTime endDate;

  @Schema(description = "제재 대상 회원 ID", example = "2001")
  private int memId;

  @Schema(description = "제재를 수행한 관리자 ID", example = "999")
  private int adminId;

  public SanctionDTO(){
  }
}
