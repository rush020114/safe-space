# 🛡️ SafeSpace - 커뮤니티 보호 시스템

<div align="center">

![Project Status](https://img.shields.io/badge/status-completed-success)
![Java](https://img.shields.io/badge/Java-17-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?logo=spring-boot)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?logo=mariadb&logoColor=white)

**욕설 필터링과 단계적 제재를 통한 건강한 커뮤니티 환경 구축**

</div>

---

## 📋 프로젝트 개요

온라인 커뮤니티에서 발생하는 욕설과 비방을 자동으로 감지하고, 단계적 제재를 통해 건강한 커뮤니케이션 환경을 만드는 시스템입니다.

### 💭 프로젝트 동기

> "예전에는 온라인 커뮤니티나 게임에서 감정적으로 반응하는 일이 많았습니다.  
> 시간이 지나면서 그런 행동이 누군가에게 상처가 될 수 있다는 걸 깨닫게 되었고,  
> 지금은 더 건강한 방향으로 나아가고자 노력하고 있습니다.  
> 이 프로젝트는 그런 고민의 연장선에서, **기술을 통해 더 나은 커뮤니케이션 환경**을 만들고 싶다는 생각으로 시작했습니다."

---

## 🎯 핵심 기능

### 1️⃣ 욕설 자동 필터링
- 한국어/영어 욕설 리스트 기반 실시간 감지
- 정규표현식을 활용한 공백 변형 처리 ("바 보", "lo ser" 등)
- 게시글/댓글 작성 시 자동 검증

### 2️⃣ 자동 신고 시스템
- 욕설 감지 시 신고 객체 자동 생성 및 DB 저장
- 중복 신고 방지 로직
- 신고 대상 구분 (게시글/댓글)

### 3️⃣ SSE 실시간 알림
- 신고 발생 시 관리자에게 실시간 알림
- EventSource 기반 단방향 통신
- JWT 인증과 SSE 통합 (URL 파라미터 방식)

### 4️⃣ 단계적 제재 시스템
- **3회 경고**: 1분 정지
- **6회 경고**: 3분 정지  
- **9회 경고**: 영구 정지
- Redis 기반 실시간 제재 상태 관리

### 5️⃣ Redis 캐싱 최적화
- 제재 정보를 Redis Hash 구조로 저장
- 매 요청마다 DB 조회 없이 Redis에서 빠르게 확인
- 스케줄러를 통한 DB-Redis 동기화

### 6️⃣ JWT 인증 & 제재 연동
- Spring Security + JWT 기반 인증
- 로그인 시 제재 상태 확인
- 제재 중인 사용자 즉시 차단

---

## 🛠 기술 스택

### Backend
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.11-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=spring-security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=json-web-tokens&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-000000?style=flat-square&logo=mybatis&logoColor=white)

### Database & Cache
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)

### Tools
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=flat-square)

---

## 🏗️ 시스템 아키텍처
```
┌─────────────┐      ┌──────────────────┐      ┌─────────────┐
│   Client    │─────>│  Spring Boot     │─────>│   MariaDB   │
│  (게시글    │      │  + Security      │      │ (게시글/    │
│   작성)     │      │  + JWT           │      │  회원/제재) │
└─────────────┘      └──────────────────┘      └─────────────┘
                              │                        ↑
                              │                        │
                              ↓                        │
                     ┌─────────────────┐              │
                     │ ProfanityFilter │              │
                     │  (욕설 감지)    │              │
                     └─────────────────┘              │
                              │                        │
                              ↓                        │
                     ┌─────────────────┐              │
                     │  ReportService  │──────────────┘
                     │   (신고 처리)   │
                     └─────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ↓                   ↓
           ┌─────────────────┐  ┌─────────────────┐
           │ SseEmitterService│  │  RedisService   │
           │  (실시간 알림)   │  │ (제재 캐싱)     │
           └─────────────────┘  └─────────────────┘
                    │                   │
                    ↓                   ↓
           ┌─────────────────┐  ┌─────────────────┐
           │  관리자 대시보드 │  │     Redis       │
           │   (SSE 구독)    │  │ (Hash 구조)     │
           └─────────────────┘  └─────────────────┘
```

---

## 📡 주요 API 명세

<details>
<summary><b>🔐 인증 API</b></summary>

### 로그인
```http
POST /member/login

Request Body:
{
  "memEmail": "user@example.com",
  "memPw": "password123"
}

Response: 200 OK
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

제재 중인 경우:
Response: 403 Forbidden
{
  "error": "계정이 정지되었습니다",
  "reason": "신고 3회 누적으로 2025-11-14 15:30:00까지 정지"
}
```

</details>

<details>
<summary><b>📝 게시글/댓글 API</b></summary>

### 게시글 작성 (욕설 필터링 적용)
```http
POST /posts

Request Body:
{
  "postTitle": "제목",
  "postContent": "내용"
}

욕설 포함 시:
Response: 400 Bad Request
{
  "error": "욕설이 포함되어 있습니다"
}

정상 작성 시:
Response: 201 Created
→ 욕설 감지 시 자동 신고 생성
→ SSE를 통해 관리자에게 실시간 알림 전송
```

</details>

<details>
<summary><b>🚨 신고 API</b></summary>

### 관리자 신고 목록 조회
```http
GET /admin/reports?targetType=POST

Response: 200 OK
[
  {
    "reportId": 1,
    "reportReason": "욕설 사용",
    "reportStatus": "PENDING",
    "targetType": "POST",
    "postDTO": {
      "postId": 5,
      "postContent": "욕설이 포함된 내용",
      "memId": 10
    },
    "reportDate": "2025-11-14T10:30:00"
  }
]
```

### 신고 처리 (승인/거절)
```http
POST /admin/reports/handle

Request Body:
{
  "reportId": 1,
  "reportStatus": "APPROVED",
  "targetType": "POST"
}

Response: 200 OK
→ 신고 승인 시 자동으로:
  1. 게시글/댓글 필터링 (블라인드 처리)
  2. 회원 경고 횟수 증가
  3. 경고 횟수에 따라 제재 적용 (3회, 6회, 9회)
  4. Redis에 제재 상태 즉시 반영
  5. Sanction 테이블에 제재 기록 저장
```

</details>

<details>
<summary><b>🔔 SSE 실시간 알림 API</b></summary>

### 관리자 SSE 연결
```http
GET /admin/reports/stream?token=Bearer%20eyJhbGciOiJIUzI1NiJ9...

Response: 200 OK
Content-Type: text/event-stream

연결 성공 이벤트:
event: connect
data: SSE 연결 성공

신고 발생 이벤트:
event: newReport
data: {
  "reportId": 5,
  "reportReason": "욕설 사용",
  "targetType": "POST",
  "reportDate": "2025-11-14T15:00:00"
}
```

**💡 기술 선택 이유**
- WebSocket 대신 SSE를 선택한 이유:
  - 관리자만 단방향 알림을 받으면 되는 구조
  - HTTP 기반으로 구현이 간단
  - 자동 재연결 기능 내장

**🔐 보안 트러블슈팅**
- 문제: SSE는 Header에 토큰을 담을 수 없어 Spring Security 인증 실패
- 해결: 
  1. URL 파라미터로 토큰 전달
  2. `JwtConfirmFilter`에서 URL 파라미터 토큰 추출 로직 추가
  3. `@PreAuthorize("hasRole('ADMIN')")`로 권한 검증

</details>

---

## 💡 핵심 구현 코드

<details>
<summary><b>1️⃣ 욕설 필터링 구현</b></summary>

### ProfanityFilter.java
```java
@Slf4j
@Component
public class ProfanityFilter {
  // 욕설 단어 리스트 (한국어 + 영어)
  private static final List<String> PROFANITY_LIST = Arrays.asList(
    "바보", "멍청", "loser", "idiot" // ...
  );

  private final Pattern profanityPattern;

  public ProfanityFilter() {
    String regex = String.join("|", PROFANITY_LIST);
    this.profanityPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
  }

  // 욕설 정규화 (공백 제거, 소문자 변환)
  public String preprocess(String text) {
    return text
      .replaceAll("\\s+", "")      // "바 보" → "바보"
      .toLowerCase();               // "LOSER" → "loser"
  }

  // 욕설 포함 여부 확인
  public boolean containsProfanity(String input) {
    String cleaned = preprocess(input);
    return profanityPattern.matcher(cleaned).find();
  }
}
```

**구현 특징**
- ✅ "바 보", "바     보" 같은 공백 변형 처리
- ✅ "lo ser", "LOSER" 같은 대소문자 변형 처리
- ❌ "바1보", "바아아보" 같은 의도적 변형은 미처리 (기능 구현에 집중)

**향후 개선 방향**
- AI 기반 필터링 도입 시 이 경험이 기반이 될 것

</details>

<details>
<summary><b>2️⃣ Redis 제재 캐싱 구현</b></summary>

### RedisService.java - Hash 구조 활용
```java
@Service
@RequiredArgsConstructor
public class RedisService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final MemberMapper memberMapper;

  // Redis에서 회원 제재 정보 조회
  public MemberDTO getMemberSanctionInfo(int memId) {
    String key = "member:" + memId;
    
    // Redis Hash 전체 조회
    Map<Object, Object> cachedData = redisTemplate.opsForHash().entries(key);
    
    if (!cachedData.isEmpty()) {
      // 캐시 히트 - Redis에서 바로 반환
      return convertToMemberDTO(cachedData);
    }
    
    // 캐시 미스 - DB 조회 후 Redis에 캐싱
    MemberDTO memberDTO = memberMapper.getMemberStatusById(memId);
    if (memberDTO != null) {
      cacheMemberSanctionInfo(memId, memberDTO);
    }
    return memberDTO;
  }

  // 제재 시 Redis 즉시 업데이트
  public void updateMemberStatus(int memId, String newStatus, 
                                  LocalDateTime bannedUntil,
                                  String sanctionType, 
                                  String sanctionReason) {
    String key = "member:" + memId;
    
    Map<String, String> updates = new HashMap<>();
    updates.put("status", newStatus);
    updates.put("bannedUntil", bannedUntil != null ? bannedUntil.format(FORMATTER) : "");
    updates.put("sanctionType", sanctionType != null ? sanctionType : "");
    updates.put("sanctionReason", sanctionReason != null ? sanctionReason : "");
    
    // Hash 필드별 업데이트
    redisTemplate.opsForHash().putAll(key, updates);
    redisTemplate.expire(key, 24, TimeUnit.HOURS);
  }
}
```

**설계 결정 과정**
1. **초기 설계**: `Map<String, String>` 구조로 저장
   - 문제: member 테이블의 active, banned만 저장 가능
   - 한계: 3회, 6회, 9회 경고에 따른 분기 처리 불가

2. **개선 설계**: **Redis Hash 구조 도입**
   - `user:{id}` 키에 여러 필드 저장
   - 필드: status, reason, banDate, warnCount, sanctionType
   - 효과: 필드 단위 접근으로 유연한 분기 처리 가능

**캐싱 전략**
- TTL 24시간 설정
- 이유:
  1. 제재 상태 변경 시 `updateMemberStatus()`가 즉시 호출되어 Redis 갱신
  2. 제재 정보는 보안상 민감하지 않음
  3. 24시간 동안 DB 부하 감소 효과 극대화

</details>

<details>
<summary><b>3️⃣ 단계적 제재 로직 구현</b></summary>

### ReportService.java - 경고 횟수에 따른 제재
```java
@Transactional(rollbackFor = Exception.class)
public void handleReport(ReportDTO reportDTO, int adminId) {
  // 1. 신고 승인 처리
  reportMapper.handleReport(reportDTO);
  
  // 2. 회원 경고 횟수 증가
  memberMapper.addWarningCnt(targetMemId);
  
  // 3. 콘텐츠 필터링 (블라인드 처리)
  if (isPost) {
    postMapper.filterPost(targetContentId);
  } else {
    commentMapper.filterComment(targetContentId);
  }
  
  // 4. 경고 횟수 조회 후 제재 적용
  int warningCnt = memberMapper.getMemberStatusById(targetMemId).getWarningCnt();
  
  if (warningCnt >= 9) {
    // 영구 정지
    bannedUntil = LocalDateTime.of(2099, 12, 31, 23, 59);
    sanctionType = "BAN_PERMANENT";
    sanctionReason = "신고 9회 누적으로 영구 정지";
    
    sanctionMapper.regSanction(sanctionDTO);
    memberMapper.banMember(bannedUntilStr, targetMemId);
    redisService.updateMemberStatus(targetMemId, "BANNED", bannedUntil, sanctionType, sanctionReason);
    
  } else if (warningCnt == 6) {
    // 3분 정지
    bannedUntil = LocalDateTime.now().plusMinutes(3);
    sanctionType = "BAN_TEMP_2";
    sanctionReason = "신고 6회 누적으로 " + bannedUntil + "까지 정지";
    // ... (제재 처리 동일)
    
  } else if (warningCnt == 3) {
    // 1분 정지
    bannedUntil = LocalDateTime.now().plusMinutes(1);
    sanctionType = "BAN_TEMP_1";
    sanctionReason = "신고 3회 누적으로 " + bannedUntil + "까지 정지";
    // ... (제재 처리 동일)
    
  } else {
    // 경고만 (제재 없음)
    log.info("경고 {}회 - 제재 없음 (콘텐츠 필터링만)", warningCnt);
    // Redis 경고 횟수만 업데이트
    redisService.cacheMemberSanctionInfo(targetMemId, updated);
  }
}
```

**트랜잭션 고려사항**
- 원래는 `sanctionService`에 제재 로직을 분리하고 싶었음
- 하지만 `reportService`에서 트랜잭션을 유지해야 했기 때문에
- 핵심 기능에 집중하기 위해 `reportService`에서 처리

</details>

<details>
<summary><b>4️⃣ JWT 인증 + 제재 상태 확인</b></summary>

### LoginFilter.java - 로그인 시 제재 확인
```java
@Override
protected void successfulAuthentication(HttpServletRequest request, 
                                         HttpServletResponse response,
                                         FilterChain chain, 
                                         Authentication authResult) {
  // 토큰 생성을 위한 정보 추출
  CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
  MemberDTO memberDTO = userDetails.getMemberDTO();
  int memId = memberDTO.getMemId();

  // Redis에서 제재 상태 확인
  MemberDTO memberInfo = redisService.getMemberSanctionInfo(memId);
  if ("BANNED".equals(memberInfo.getMemStatus())) {
    SanctionDTO sanctionDTO = memberInfo.getSanctionDTO();
    String sanctionReason = sanctionDTO.getSanctionReason();
    
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");
    
    String errorMessage = String.format(
      "{\"error\":\"계정이 정지되었습니다\",\"reason\":\"%s\"}",
      sanctionReason
    );
    response.getWriter().write(errorMessage);
    return;  // 로그인 차단
  }

  // 제재가 없으면 JWT 토큰 발급
  String accessToken = jwtUtil.createJwt(username, role, memId, (1000 * 60 * 20));
  response.setHeader("Authorization", "Bearer " + accessToken);
}
```

### JwtConfirmFilter.java - 요청마다 제재 확인
```java
@Override
protected void doFilterInternal(HttpServletRequest request, 
                                 HttpServletResponse response,
                                 FilterChain filterChain) {
  // 1. 헤더에서 토큰 추출
  String token = null;
  String authorization = request.getHeader("Authorization");
  if (authorization != null && authorization.startsWith("Bearer ")) {
    token = authorization.split(" ")[1];
  }
  
  // 2. SSE 요청인 경우 URL에서 토큰 추출
  if (token == null && request.getRequestURL().toString().contains("/admin/reports/stream")) {
    String paramToken = request.getParameter("token");
    if (paramToken != null && paramToken.startsWith("Bearer ")) {
      token = paramToken.substring(7);
    }
  }
  
  // 3. 토큰 유효성 검증
  if (token != null && !jwtUtil.isExpired(token)) {
    int memId = jwtUtil.getMemIdFromToken(token);
    
    // 4. Redis에서 제재 상태 확인
    MemberDTO memberInfo = redisService.getMemberSanctionInfo(memId);
    if ("BANNED".equals(memberInfo.getMemStatus())) {
      // 제재된 사용자 차단
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"error\":\"계정이 정지되었습니다\"}");
      return;
    }
    
    // 5. 인증 정보 SecurityContext에 저장
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }
  
  filterChain.doFilter(request, response);
}
```

**제재 확인 흐름**
1. **로그인 시**:
   - `loginFilter`에서 `authenticationManager`로 사용자 인증
   - 검증된 사용자 정보로 Redis에서 제재 상태 확인
   - 제재 중이면 로그인 차단 + 사유 포함 응답

2. **요청마다**:
   - `jwtConfirmFilter`에서 토큰 검증
   - Redis에서 제재 상태 확인
   - 제재 중이면 요청 차단

</details>

<details>
<summary><b>5️⃣ 스케줄러를 통한 제재 자동 해제</b></summary>

### SanctionScheduler.java
```java
@Component
@RequiredArgsConstructor
public class SanctionScheduler {
  private final MemberMapper memberMapper;
  private final RedisTemplate<String, Object> redisTemplate;

  // 매분마다 만료된 정지 해제
  @Scheduled(cron = "0 * * * * *")  // 매분 0초
  public void releaseExpiredBans() {
    LocalDateTime now = LocalDateTime.now();
    
    // 1. DB에서 해제 대상 조회
    List<MemberDTO> expiredMembers = memberMapper.getExpiredBannedMembers(now);
    
    for (MemberDTO member : expiredMembers) {
      int memId = member.getMemId();
      
      // 2. DB 상태 업데이트
      memberMapper.updateMemberStatus(memId, "ACTIVE");
      
      // 3. Redis 동기화 (키가 있으면)
      String redisKey = "member:" + memId;
      if (redisTemplate.hasKey(redisKey)) {
        Map<String, String> updates = Map.of(
          "status", "ACTIVE",
          "bannedUntil", ""
        );
        redisTemplate.opsForHash().putAll(redisKey, updates);
      }
    }
  }
}
```

**설계 결정**
- 실시간 처리 대신 스케줄러로 해결
- 이유:
  1. 제재 해제는 초 단위 정확성이 불필요
  2. 1분 단위 확인으로도 충분한 UX
  3. 실시간 시스템 도입 없이 데이터 일관성 유지 가능

</details>

<details>
<summary><b>6️⃣ SSE 실시간 알림 구현</b></summary>

### SseEmitterService.java
```java
@Service
public class SseEmitterService {
  // 관리자들의 SSE 연결을 저장할 Map
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  // 관리자 SSE 연결 생성
  public SseEmitter createSseEmitter(String adminId) {
    SseEmitter emitter = new SseEmitter(180000L);  // 3분 타임아웃
    emitters.put(adminId, emitter);
    
    // 연결 종료/타임아웃/에러 시 제거
    emitter.onCompletion(() -> emitters.remove(adminId));
    emitter.onTimeout(() -> emitters.remove(adminId));
    emitter.onError((e) -> emitters.remove(adminId));
    
    // 연결 확인용 더미 데이터 전송
    try {
      emitter.send(SseEmitter.event()
        .name("connect")
        .data("SSE 연결 성공"));
    } catch (IOException e) {
      emitters.remove(adminId);
    }
    
    return emitter;
  }

  // 모든 관리자에게 신고 알림 전송
  public void sendToAll(Object data) {
    emitters.forEach((adminId, emitter) -> {
      try {
        emitter.send(SseEmitter.event()
          .name("newReport")
          .data(data));
      } catch (IOException e) {
        emitters.remove(adminId);
      }
    });
  }
}
```

### AdminReportController.java - SSE 엔드포인트
```java
@GetMapping("/stream")
@PreAuthorize("hasRole('ADMIN')")
public SseEmitter streamReports(@RequestParam("token") String token) {
  // URL 파라미터로 받은 토큰에서 adminId 추출
  String adminId = jwtUtil.getMemIdFromToken(token.substring(7)).toString();
  return sseEmitterService.createSseEmitter(adminId);
}
```

**기술 선택의 판단력**
- ❌ WebSocket 고려 → 양방향 통신 불필요
- ✅ SSE 선택 → 관리자만 단방향 알림을 받으면 되는 구조
- 기술을 무작정 쓰는 게 아니라, **요구사항에 맞는 최적의 기술 선택**

</details>

<details>
<summary><b>7️⃣ Enum으로 코드 품질 향상</b></summary>

### ReportStatus.java - 신고 상태 Enum
```java
public enum ReportStatus {
  PENDING,    // 대기
  APPROVED,   // 승인
  REJECTED;   // 거절

  public static ReportStatus from(String value) {
    if (value == null || value.trim().isEmpty())
      throw new IllegalArgumentException("신고 상태값은 null 또는 빈문자열일 수 없습니다.");
    
    try {
      return ReportStatus.valueOf(value.trim().toUpperCase());
    } catch (Exception e) {
      throw new IllegalArgumentException("지원하지 않는 신고 상태값입니다: " + value);
    }
  }
}
```

### ReportTargetType.java - 신고 대상 Enum
```java
public enum ReportTargetType {
  POST,      // 게시글
  COMMENT;   // 댓글

  public static ReportTargetType from(String value) {
    if (value == null || value.trim().isEmpty())
      throw new IllegalArgumentException("신고 대상 유형은 null 또는 빈문자열일 수 없습니다.");
    
    try {
      return ReportTargetType.valueOf(value.trim().toUpperCase());
    } catch (Exception e) {
      throw new IllegalArgumentException("지원하지 않는 신고 대상 유형입니다: " + value);
    }
  }
}

**Enum 사용의 효과**
- ✅ 타입 안정성 보장
- ✅ 잘못된 값 입력 방지
- ✅ 코드 가독성 향상
- ✅ IDE 자동완성 지원

</details>

---

## 🗄️ 데이터베이스 설계

### ERD
> 📸 **이미지 위치**: `images/erd.png`

### 주요 테이블 구조

#### 1. member (회원)
```sql
CREATE TABLE member (
  mem_id INT PRIMARY KEY AUTO_INCREMENT,
  mem_email VARCHAR(100) NOT NULL UNIQUE,
  mem_pw VARCHAR(255) NOT NULL,
  mem_nickname VARCHAR(50) NOT NULL,
  mem_role ENUM('USER', 'ADMIN') DEFAULT 'USER',
  mem_status ENUM('ACTIVE', 'BANNED') DEFAULT 'ACTIVE',
  warning_cnt INT DEFAULT 0,
  banned_until DATETIME NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 2. post (게시글)
```sql
CREATE TABLE post (
  post_id INT PRIMARY KEY AUTO_INCREMENT,
  mem_id INT NOT NULL,
  post_title VARCHAR(200) NOT NULL,
  post_content TEXT NOT NULL,
  is_filtered BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (mem_id) REFERENCES member(mem_id)
);
```

#### 3. comment (댓글)
```sql
CREATE TABLE comment (
  cmt_id INT PRIMARY KEY AUTO_INCREMENT,
  post_id INT NOT NULL,
  mem_id INT NOT NULL,
  cmt_content TEXT NOT NULL,
  is_filtered BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (post_id) REFERENCES post(post_id),
  FOREIGN KEY (mem_id) REFERENCES member(mem_id)
);
```

#### 4. report (신고)
```sql
CREATE TABLE report (
  report_id INT PRIMARY KEY AUTO_INCREMENT,
  mem_id INT NOT NULL,
  target_type ENUM('POST', 'COMMENT') NOT NULL,
  target_id INT NOT NULL,
  report_reason VARCHAR(255) NOT NULL,
  report_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
  report_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (mem_id) REFERENCES member(mem_id)
);
```

#### 5. sanction (제재)
```sql
CREATE TABLE sanction (
  sanction_id INT PRIMARY KEY AUTO_INCREMENT,
  mem_id INT NOT NULL,
  admin_id INT NOT NULL,
  sanction_type ENUM('BAN_TEMP_1', 'BAN_TEMP_2', 'BAN_PERMANENT') NOT NULL,
  sanction_reason VARCHAR(255) NOT NULL,
  start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  end_date DATETIME NULL,
  FOREIGN KEY (mem_id) REFERENCES member(mem_id),
  FOREIGN KEY (admin_id) REFERENCES member(mem_id)
);
```

### Redis 데이터 구조

Key: member:{memId}
Type: Hash
Fields:

status: "ACTIVE" | "BANNED"
warningCnt: "3"
bannedUntil: "2025-11-14T15:30:00"
sanctionType: "BAN_TEMP_1"
sanctionReason: "신고 3회 누적으로 2025-11-14 15:30:00까지 정지"
TTL: 24시간

---

## 🔧 프로젝트 구조

src/
├── main/
│   ├── java/com/safespace/content_filter_backend/
│   │   ├── auth/                           # 인증 관련
│   │   │   ├── filter/
│   │   │   │   ├── LoginFilter.java       # 로그인 필터
│   │   │   │   └── JwtConfirmFilter.java  # JWT 검증 필터
│   │   │   ├── service/
│   │   │   │   └── UserDetailServiceImpl.java
│   │   │   └── util/
│   │   │       └── JwtUtil.java           # JWT 생성/검증
│   │   │
│   │   ├── config/
│   │   │   ├── SecurityConfig.java        # Spring Security 설정
│   │   │   └── RedisConfig.java           # Redis 설정
│   │   │
│   │   ├── domain/
│   │   │   ├── admin/                     # 관리자 기능
│   │   │   │   ├── controller/
│   │   │   │   │   └── AdminReportController.java
│   │   │   │   └── service/
│   │   │   │       └── SseEmitterService.java  # SSE 실시간 알림
│   │   │   │
│   │   │   ├── member/                    # 회원 관리
│   │   │   │   ├── dto/MemberDTO.java
│   │   │   │   ├── mapper/MemberMapper.java
│   │   │   │   └── service/MemberService.java
│   │   │   │
│   │   │   ├── post/                      # 게시글
│   │   │   │   ├── controller/PostController.java
│   │   │   │   └── service/PostService.java
│   │   │   │
│   │   │   ├── comment/                   # 댓글
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── report/                    # 신고
│   │   │   │   ├── controller/ReportController.java
│   │   │   │   ├── service/ReportService.java
│   │   │   │   └── model/
│   │   │   │       ├── ReportStatus.java      # Enum
│   │   │   │       └── ReportTargetType.java  # Enum
│   │   │   │
│   │   │   └── sanction/                  # 제재
│   │   │       ├── scheduler/
│   │   │       │   └── SanctionScheduler.java  # 제재 해제 스케줄러
│   │   │       └── service/SanctionService.java
│   │   │
│   │   └── infra/                         # 인프라 계층
│   │       ├── filtering/
│   │       │   └── ProfanityFilter.java   # 욕설 필터링
│   │       └── redis/
│   │           └── RedisService.java      # Redis 캐싱
│   │
│   └── resources/
│       ├── mapper/                        # MyBatis XML
│       │   ├── member-mapper.xml
│       │   ├── post-mapper.xml
│       │   ├── report-mapper.xml
│       │   └── sanction-mapper.xml
│       └── application.properties         # 설정 파일

---

## 🎬 주요 화면

### 1️⃣ 신고 자동 생성 플로우
> 📸 **이미지/GIF 위치**: `images/report_flow.gif`

**동작 과정**
1. 사용자가 욕설이 포함된 게시글/댓글 작성
2. `ProfanityFilter`가 욕설 감지
3. 신고 객체 자동 생성 및 DB 저장
4. SSE를 통해 관리자에게 실시간 알림 전송

---

### 2️⃣ 관리자 실시간 알림
> 📸 **이미지/GIF 위치**: `images/admin_notification.gif`

**주요 기능**
- 💬 실시간 신고 알림 수신
- 📋 신고 목록 조회
- ✅ 신고 승인/거절 처리

---

### 3️⃣ 단계적 제재 적용
> 📸 **이미지/GIF 위치**: `images/sanction_flow.gif`

**제재 흐름**
1. 관리자가 신고 승인
2. 경고 횟수 증가
3. 경고 횟수에 따라 자동 제재
4. Redis에 제재 상태 즉시 반영
5. 제재 중인 사용자 로그인 차단

---

### 4️⃣ 제재 상태 확인
> 📸 **이미지/GIF 위치**: `images/ban_check.gif`

**로그인 차단 화면**
- 제재 사유 표시
- 제재 기간 안내

---

## 🚀 실행 방법

### 1. 사전 요구사항
```bash
- Java 17
- MariaDB 10.x
- Redis 7.x
- Gradle 8.x
```

### 2. 데이터베이스 설정
```sql
-- MariaDB 데이터베이스 생성
CREATE DATABASE safespace CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 및 권한 부여
CREATE USER 'safespace'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON safespace.* TO 'safespace'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Redis 실행
```bash
# Docker로 Redis 실행
docker run -d -p 6379:6379 redis:7-alpine

# 또는 로컬 설치 후
redis-server
```

### 4. application.properties 설정
```properties
# 데이터베이스 설정
spring.datasource.url=jdbc:mariadb://localhost:3306/safespace
spring.datasource.username=safespace
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# MyBatis 설정
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.safespace.content_filter_backend.domain

# Redis 설정
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT 설정
jwt.secret=your_secret_key_here_minimum_256_bits
jwt.expiration=1200000

# 로그 설정
logging.level.com.safespace=DEBUG
```

### 5. 프로젝트 실행
```bash
# Gradle로 빌드 및 실행
./gradlew clean build
./gradlew bootRun

# 또는 JAR 파일 실행
java -jar build/libs/content-filter-backend-0.0.1-SNAPSHOT.jar
```

### 6. 접속 확인
```bash
# 서버 실행 확인
curl http://localhost:8080/actuator/health

# 관리자 SSE 연결 테스트 (토큰 필요)
curl -N http://localhost:8080/admin/reports/stream?token=Bearer_YOUR_TOKEN
```

---

## 🧪 테스트 시나리오

### 1. 욕설 필터링 테스트
```bash
# 욕설 포함 게시글 작성
POST /posts
Content-Type: application/json
Authorization: Bearer {token}

{
  "postTitle": "테스트",
  "postContent": "바보 같은 내용"
}

# 예상 결과: 400 Bad Request + 자동 신고 생성
```

### 2. 신고 처리 및 제재 테스트
```bash
# 1단계: 3회 경고 (1분 정지)
POST /admin/reports/handle
{
  "reportId": 1,
  "reportStatus": "APPROVED"
}

# 2단계: 6회 경고 (3분 정지)
POST /admin/reports/handle
{
  "reportId": 2,
  "reportStatus": "APPROVED"
}

# 3단계: 9회 경고 (영구 정지)
POST /admin/reports/handle
{
  "reportId": 3,
  "reportStatus": "APPROVED"
}
```

### 3. Redis 캐싱 확인
```bash
# Redis CLI로 제재 정보 확인
redis-cli
> HGETALL member:1
1) "status"
2) "BANNED"
3) "warningCnt"
4) "3"
5) "bannedUntil"
6) "2025-11-14T15:30:00"
7) "sanctionType"
8) "BAN_TEMP_1"
9) "sanctionReason"
10) "신고 3회 누적으로 2025-11-14 15:30:00까지 정지"
```

---

## 💬 기술적 의사결정 및 트러블슈팅

### 1️⃣ JWT 인증 구현 이유

**선택 배경**
> "인증과 권한 관리를 위해 Spring Security와 JWT를 도입했습니다.  
> JWT는 stateless 인증 방식으로, 로그인 상태를 서버에 저장하지 않아도 되며  
> 서버 확장성과 API 중심 구조에 적합하다는 점을 직접 구현하며 체감할 수 있었습니다."

**구현 내용**
- Spring Security + JWT 기반 인증
- Access Token 20분 유효기간
- 제재 상태와 JWT 연동

---

### 2️⃣ Redis 설계 과정

**문제 상황**
- 초기: `Map<String, String>` 구조로 설계
- 한계: member 테이블의 active, banned 상태만 저장 가능
- 결과: 3회, 6회, 9회 경고에 따른 분기 처리 불가

**해결 방법**
- Redis **Hash 구조** 도입
- `user:{id}` 키에 여러 필드 저장
- 필드: status, reason, banDate, warnCount, sanctionType
- 효과: 필드 단위 접근으로 유연한 분기 처리 가능

**설계 원칙**
> "제재 로직은 매 요청마다 확인이 필요하지만 상태 변화는 드물기 때문에,  
> Redis 캐싱 구조에 가장 적합하다고 판단했습니다."

---

### 3️⃣ SSE 선택 과정

**기술 비교**
| 기술 | 장점 | 단점 | 적합성 |
|------|------|------|--------|
| **WebSocket** | 양방향 통신 | 구현 복잡, 불필요한 기능 | ❌ 오버스펙 |
| **SSE** | HTTP 기반, 단방향 | 브라우저 호환성 | ✅ 최적 |
| **Polling** | 간단 | 서버 부하 | ❌ 비효율 |

**선택 이유**
> "처음엔 WebSocket을 고려했지만,  
> 관리자만 단방향 알림을 받으면 되는 구조라 SSE가 더 적합하다고 판단했습니다.  
> 기술을 무작정 쓰는 게 아니라, **요구사항에 맞는 최적의 기술을 선택**한 경험입니다."

**SSE 보안 문제 해결**
- 문제: SSE는 Header에 토큰을 담을 수 없어 Spring Security 인증 실패
- 해결:
  1. `/admin/reports/stream` 경로에 `.permitAll()` 설정
  2. URL 파라미터로 토큰 전달
  3. `JwtConfirmFilter`에서 URL 파라미터 토큰 추출 및 검증
  4. `@PreAuthorize("hasRole('ADMIN')")`로 권한 보장

---

### 4️⃣ 트랜잭션 설계 고민

**문제 상황**
- 원래는 `sanctionService`에 제재 로직을 분리하고 싶었음
- 하지만 `reportService`에서 트랜잭션을 유지해야 했기 때문에

**해결**
- 핵심 기능에 집중하기 위해 `reportService`에서 처리
- 향후 리팩토링 시 서비스 분리 고려

---

### 5️⃣ 스케줄러 vs 실시간 처리

**설계 결정**
- 제재 해제는 **스케줄러**로 처리 (매분 실행)
- 실시간 처리 대신 1분 단위 확인

**선택 이유**
1. 제재 해제는 초 단위 정확성이 불필요
2. 1분 단위 확인으로도 충분한 UX
3. 실시간 시스템 도입 없이 데이터 일관성 유지 가능

---

## 🎓 배운 점 및 성장

### 기술적 성장
1. **Redis Hash 구조 이해**
   - 단순 Key-Value를 넘어 복잡한 데이터 구조 설계
   - 캐싱 전략과 TTL 관리 경험

2. **Spring Security 심화**
   - Filter Chain 동작 원리 이해
   - JWT와 제재 시스템 통합 경험

3. **SSE 실시간 통신**
   - WebSocket과의 차이점 이해
   - 보안 문제 해결 경험

4. **트랜잭션 관리**
   - 서비스 간 트랜잭션 전파 이해
   - 실무적 타협점 찾기

### 아키텍처 설계 능력
1. **기술 선택 판단력**
   - 요구사항에 맞는 최적의 기술 선택
   - 오버 엔지니어링 지양

2. **성능 최적화**
   - DB 부하를 줄이기 위한 Redis 캐싱
   - 캐시 히트율 고려한 설계

3. **확장 가능한 구조**
   - Enum을 활용한 타입 안정성
   - 레이어드 아키텍처 적용

---

## 🔮 향후 개선 방향

### 1. AI 기반 필터링 도입
- 현재: 단순 키워드 매칭
- 개선: 자연어 처리 모델 활용
- 효과: "바1보", "바아아보" 같은 변형 감지 가능

### 2. Refresh Token 도입
- 현재: Access Token만 사용 (20분)
- 개선: Refresh Token 추가
- 효과: 사용자 편의성 향상

### 3. 제재 이력 대시보드
- 관리자용 통계 페이지
- 제재 추이 시각화
- 욕설 빈도 분석

### 4. 사용자 이의제기 시스템
- 제재에 대한 이의제기 기능
- 관리자 재검토 프로세스

---

## 📄 라이선스

이 프로젝트는 개인 포트폴리오 목적으로 제작되었습니다.

---

## 👨‍💻 개발자

**이름**: [Your Name]  
**이메일**: [Your Email]  
**GitHub**: [Your GitHub]  
**블로그**: [Your Blog]

---

## 📞 문의

프로젝트에 대한 문의사항이나 피드백은 언제든 환영합니다!

- 📧 Email: [your.email@example.com]
- 💬 GitHub Issues: [Repository Issues Link]

---

<div align="center">

**"기술을 통해 더 나은 커뮤니케이션 환경을 만들다"**

Made with ❤️ by [Your Name]

</div>
