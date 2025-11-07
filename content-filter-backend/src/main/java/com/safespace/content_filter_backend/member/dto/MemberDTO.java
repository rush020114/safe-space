package com.safespace.content_filter_backend.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class MemberDTO {
  private int memId;
  private String memEmail;
  private String memPw;
  private String memName;
  private String memRole;
  private LocalDateTime createdAt;

  public MemberDTO(){
  }
}
