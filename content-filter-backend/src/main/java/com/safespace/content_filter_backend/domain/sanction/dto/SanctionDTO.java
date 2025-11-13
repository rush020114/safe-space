package com.safespace.content_filter_backend.domain.sanction.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class SanctionDTO {
  private int sanctionId;
  private String sanctionType;
  private String sanctionReason;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private int memId;
  private int adminId;

  public SanctionDTO(){
  }
}
