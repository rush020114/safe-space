package com.safespace.content_filter_backend.profanity.util;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import scala.collection.Seq;

import java.util.stream.Collectors;

public class KoreanTextUtil {
  public static String normalize(String text){
    // 1. 정규화
    CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

    // 2. 토큰화
    Seq<org.openkoreantext.processor.tokenizer.KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

    // 3. 토큰을 문자열로 변환 후 공백 없이 이어붙이기
    return scala.collection.JavaConverters.seqAsJavaList(tokens).stream()
            .map(token -> token.text())
            .collect(Collectors.joining());

  }
}
