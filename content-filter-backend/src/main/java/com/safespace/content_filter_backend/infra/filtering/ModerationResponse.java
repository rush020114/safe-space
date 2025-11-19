package com.safespace.content_filter_backend.infra.filtering;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class ModerationResponse {
  private String id;
  private String model;
  private List<Result> results;

  //  ModerationResponse 안에 Result가 필요하긴 하지만
  //  Result가 ModerationResponse에 완전히 종속되게 만들고 싶진 않을 때
  //  중첩 클래스를 적용(논리적 결합)하되 외부 클래스에 의존되진 않게(물리적 독립) 한다.
  @Data
  public static class Result{
    private boolean flagged;
    private Categories categories;

    @JsonProperty("category_scores")
    private CategoryScores categoryScores;

    // Categories는 Result 안에서만 의미 있음
    @Data
    public static class Categories {
      private boolean sexual;
      private boolean hate;
      private boolean harassment;
      private boolean violence;

      // JSON 필드명이 "self-harm"이라 @JsonProperty 필요
      @JsonProperty("self-harm")
      private boolean selfHarm;

      @JsonProperty("sexual/minors")
      private boolean sexualMinors;

      @JsonProperty("hate/threatening")
      private boolean hateThreatening;

      @JsonProperty("violence/graphic")
      private boolean violenceGraphic;
    }

    @Data
    public static class CategoryScores {
      private double sexual;
      private double hate;
      private double harassment;
      private double violence;

      @JsonProperty("self-harm")
      private double selfHarm;
    }
  }
}
