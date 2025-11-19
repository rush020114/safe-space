package com.safespace.content_filter_backend.infra.filtering;

import com.safespace.content_filter_backend.infra.exception.ContentFilterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class OpenAIModerationService {

  @Value("${openai.api.key}")
  private String apiKey;

  @Value("${openai.api.url}")
  private String apiUrl;

  private final WebClient webClient;

  // webClient 초기화
  public OpenAIModerationService(){
    this.webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .build();
  }

  /**
   * OpenAI Moderation API로 텍스트 검사
   * @param text 검사할 텍스트
   * @return 부적절한 콘텐츠 여부
   */
  public boolean isProfanity(String text) {
    try {
      log.info("OpenApi 호출 시작 : {}", text);

      Map<String, Object> requestBody = Map.of("input", text);

      ModerationResponse response = webClient.post()
              .uri("/moderations")
              .header("Authorization", "Bearer " + apiKey)
              .header("Content-Type", "application/json")
              .bodyValue(requestBody)
              .retrieve()
              .bodyToMono(ModerationResponse.class)
              .block();

      if(response != null && !response.getResults().isEmpty()){
        boolean flagged = response.getResults().get(0).isFlagged();
        log.info("OpenAI 검사 결과: flagged={}", flagged);
        return flagged;
      }
      return false;
    } catch (Exception e){
      log.error("OpenAI API 호출 실패: {}", e.getMessage());
      throw new ContentFilterException("욕설 필터링 실패", e);
    }
  }
}
