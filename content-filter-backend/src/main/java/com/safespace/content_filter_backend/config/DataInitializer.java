package com.safespace.content_filter_backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 초기 데이터를 자동으로 생성하는 클래스
 * 관리자 계정이 없으면 자동으로 생성함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("🔍 초기 데이터 확인 중...");

        try {
            // ADMIN 권한을 가진 사용자가 있는지 확인
            Integer adminCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM MEMBER WHERE MEM_ROLE = 'ROLE_ADMIN'",
                    Integer.class
            );

            if (adminCount != null && adminCount == 0) {
                // 관리자 계정이 없으면 생성
                String encodedPassword = passwordEncoder.encode("1111");

                jdbcTemplate.update(
                        "INSERT INTO MEMBER (MEM_EMAIL, MEM_PW, MEM_NAME, MEM_ROLE, MEM_STATUS) VALUES (?, ?, ?, ?, ?)",
                        "admin",
                        encodedPassword,
                        "관리자",
                        "ROLE_ADMIN",
                        "ACTIVE"
                );

                log.info("✅ 초기 관리자 계정 생성 완료!");
                log.info("📧 이메일: admin");
                log.info("🔑 비밀번호: 1111");
            } else {
                log.info("✅ 관리자 계정이 이미 존재합니다. ({}명)", adminCount);
            }

        } catch (Exception e) {
            log.error("❌ 초기 데이터 생성 중 오류 발생: {}", e.getMessage());
            // 에러가 나도 애플리케이션 시작은 계속 진행
        }
    }
}
