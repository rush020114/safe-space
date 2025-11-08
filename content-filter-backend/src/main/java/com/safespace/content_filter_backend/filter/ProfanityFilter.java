package com.safespace.content_filter_backend.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProfanityFilter {
  // 욕설 단어 리스트
  // static이기 때문에 생성자가 바로 접근 가능
  private static final List<String> PROFANITY_LIST = Arrays.asList(
          "바보", "멍청이", "돌아이", "미친놈", "개같은", "지랄", "시발", "씨발", "좆", "병신", "새끼",
          "fuck", "shit", "bitch", "asshole", "bastard", "damn", "crap", "dick", "piss", "slut"
  );

  // 정규 표현식을 다루기 위한 클래스(문자열 형태의 정규식을 Pattern 객체로 반환)
  private final Pattern profanityPattern;

  public ProfanityFilter(){
    // 생성자에서 단어 경계, 변형 대응 등 정규식 가공
    String regex = PROFANITY_LIST.stream()
            .map(word -> {
              if (word.matches(".*[ㄱ-ㅎㅏ-ㅣ]+.*")) {
                // 초성 변형 대응: ㅅ[\\s\\*\\-]*ㅂ 같은 패턴으로 확장
                return word.replaceAll("([ㄱ-ㅎ])([ㄱ-ㅎ])", "$1[\\\\s\\\\*\\\\-]*$2");
              } else {
                // 일반 단어는 단어 경계 추가
                return "\\b" + word + "\\b";
              }
            })
            .collect(Collectors.joining("|"));

    // regex의 정규식으로 판단할 수 있는 단어의 대소문자 구분 없이 검사 가능(CASE_INSENSITIVE)
    this.profanityPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
  }

  // 욕설 포함 여부 확인
  public boolean containsProfanity (String text){
    log.info("욕설 확인 : {}", text);
    if(text == null || text.isEmpty()) {
      return false;
    }
    return profanityPattern.matcher(text).find();
  }
}
