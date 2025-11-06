package com.safespace.content_filter_backend.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
  private String memEmail;
  private String memPw;
  private String memName;
  private String memRole;
}
