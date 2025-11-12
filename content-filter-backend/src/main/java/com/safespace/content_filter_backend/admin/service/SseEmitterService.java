package com.safespace.content_filter_backend.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseEmitterService {

  // 관리자들의 SSE 연결을 저장할 Map
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  /**
   * 관리자 SSE 연결 생성
   * @param adminId 관리자 ID
   * @return SseEmitter
   */
  public SseEmitter createSseEmitter(String adminId){
    // 30분 타임아웃 (관리자가 일할 시간)
    SseEmitter emitter = new SseEmitter(180000L);

    emitters.put(adminId, emitter);

    // 연결 종료 시 제거
    emitter.onCompletion(() -> {
      log.info("SSE 연결 종료: {}", adminId);
      emitters.remove(adminId);
    });

    // 타임 아웃 시 제거
    emitter.onTimeout(() -> {
      log.info("SSE 타임아웃: {}", adminId);
      emitters.remove(adminId);
    });

    // 에러 시 제거
    emitter.onError((e) -> {
      log.info("SSE 에러: {}", adminId);
      emitters.remove(adminId);
    });

    // 연결 확인용 더미 데이터
    try {
      log.info("SSE 연결 확인용 더미 이벤트 전송 시작: {}", adminId);
      emitter.send(SseEmitter.event()
              .name("connect")
              .data("SSE 연결 성공"));
      log.info("SSE 연결 확인용 더미 이벤트 전송 완료: {}", adminId);
    } catch (IOException e) {
      log.error("SSE 연결 실패: {}", adminId, e);
      emitters.remove(adminId);
    }

    return emitter;
  }

  /**
   * 모든 관리자에게 신고 알림 전송
   * @param data 전송할 데이터
   */
  public void sendToAll(Object data){
    emitters.forEach((adminId, emitter) -> {
      log.info("SSE 알림 전송 대상 emitter 수: {}", emitters.size());
      try {
        emitter.send(SseEmitter.event()
                .name("newReport")
                .data(data));
        log.info("신고 알림 전송 성공: {}", adminId);
      } catch (IOException e){
        log.error("신고 알림 전송 실패: {}", adminId, e);
        emitters.remove(adminId);
      }
    });
  }
}
