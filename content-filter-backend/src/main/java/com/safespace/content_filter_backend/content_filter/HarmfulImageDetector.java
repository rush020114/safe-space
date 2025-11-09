package com.safespace.content_filter_backend.content_filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Hugging Face API를 활용한 유해 이미지 감지 컴포넌트
 * 사용 모델: Falconsai/nsfw_image_detection
 * - NSFW(Not Safe For Work) 이미지 분류 모델
 * - 입력: 이미지 바이너리
 * - 출력: [{"label": "nsfw", "score": 0.95}, {"label": "normal", "score": 0.05}]
 */
@Slf4j
@Component
public class HarmfulImageDetector {

  private static final String API_URL =
          "https://router.huggingface.co/hf-inference";

  /**
   * NSFW 판정 임계값
   * - 70% 이상일 경우 유해 이미지로 판단
   * - 근거: 일반적인 콘텐츠 필터링 시스템의 권장 임계값 (0.6~0.8)
   */
  private static final double NSFW_THRESHOLD = 0.7;

  @Value("${huggingface.api-key}")
  private String apiKey;

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * 이미지의 유해성 여부를 판단
   *
   * @param file 검사할 이미지 파일
   * @return true: 유해 이미지, false: 안전 이미지
   * @throws RuntimeException API 호출 실패 또는 파일 읽기 실패 시
   */
  public boolean isHarmful(MultipartFile file) {
    try {
      // 1. HTTP 헤더 설정 (인증 + Content-Type)
      HttpHeaders headers = new HttpHeaders();
      log.debug("💡 API 키 확인: {}", apiKey);
      headers.set("Authorization", "Bearer " + apiKey);
      headers.set("X-HF-Model", "Falconsai/nsfw_image_detection");
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  // 바이너리 데이터 전송

      // 2. 요청 바디 생성 (이미지 바이트 배열)
      log.debug("📦 이미지 바이트 크기: {}", file.getBytes().length);
      HttpEntity<byte[]> request =
              new HttpEntity<>(file.getBytes(), headers);
      log.debug("📨 요청 헤더: {}", headers);

      // 3. Hugging Face API 호출
      ResponseEntity<List> response =
              restTemplate.postForEntity(API_URL, request, List.class);
      log.debug("✅ 응답 상태 코드: {}", response.getStatusCode());
      log.debug("📄 응답 바디: {}", response.getBody());

      // 4. 응답 파싱 및 분석
      // 응답 예시: [{"label": "nsfw", "score": 0.95}, {"label": "normal", "score": 0.05}]
      List<Map<String, Object>> results = response.getBody();

      // 5. NSFW 라벨의 신뢰도 점수 확인
      for (Map<String, Object> result : results) {
        if ("nsfw".equals(result.get("label"))) {
          double score = (double) result.get("score");

          if (score > NSFW_THRESHOLD) {
            log.warn("유해 이미지 감지 - 파일명: {}, NSFW 점수: {}",
                    file.getOriginalFilename(), score);
            return true;
          }
        }
      }

      log.info("안전 이미지 확인 - 파일명: {}", file.getOriginalFilename());
      return false;

    } catch (Exception e) {
      // API 호출 실패 시 로깅 및 예외 전파
      // TODO: 프로덕션 환경에서는 폴백 전략 고려 (기본 승인/거부)
      log.error("이미지 검사 실패 - 파일명: {}", file.getOriginalFilename(), e);
      log.error("❌ API 호출 실패: {}", e.getMessage(), e);
      throw new RuntimeException("이미지 검사 중 오류 발생", e);
    }
  }
}