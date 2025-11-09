package com.safespace.content_filter_backend.content_filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ProfanityFilter {
  // 욕설 단어 리스트
  // static이기 때문에 생성자가 바로 접근 가능
  private static final List<String> PROFANITY_LIST = Arrays.asList(
          // ✅ 한국어 욕설 및 비속어
          "바보", "멍청", "멍청이", "돌아이", "미친놈", "개같은", "지랄", "시발", "씨발", "씹새끼", "좆", "병신", "새끼",
          "존나", "엿같은", "꺼져", "닥쳐", "죽어", "등신", "후레자식", "쌍놈", "쌍년", "개새끼", "개년", "개자식",
          "ㅅㅂ", "ㅂㅅ", "ㅄ", "ㅈㄹ", "ㄷㅊ", "ㅈㅅ", "ㅈㄴ", "ㅊㅈ", "ㄱㅊ", "ㅅㄲ",

          // ✅ 영어 욕설 및 비속어
          "fuck", "shit", "bitch", "asshole", "bastard", "damn", "crap", "dick", "piss", "slut",
          "motherfucker", "sonofabitch", "jerk", "moron", "idiot", "retard", "screw", "whore", "loser", "freak"
  );

  private final Pattern profanityPattern;

  public ProfanityFilter() {
    String regex = String.join("|", PROFANITY_LIST);
    this.profanityPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
  }

  // 욕설 정규화
  public String preprocess(String text) {
    return text
            .replaceAll("\\s+", "")                  // 공백 제거
            .toLowerCase();                          // 소문자 통일
  }

  // 욕설 포함 여부 확인
  public boolean containsProfanity (String input){
    String cleaned = preprocess(input);
    log.info("욕설 확인 : {}", input);

    return profanityPattern.matcher(cleaned).find();
  }
}
