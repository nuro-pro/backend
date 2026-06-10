# CLAUDE.md — nuro-be (AI 피부 진단 백엔드)

> Claude Code가 매 세션 자동으로 읽는다. **신규 코드는 이 문서의 룰을 따른다.** 기존 코드와 충돌하면 **본 문서가 우선**이며 기존 코드는 점진적 마이그레이션 대상. 룰을 어길 정당한 사유가 있으면 PR 설명에 명시한다.
>
> ⚠️ 이 프로젝트는 **초기 단계(그린필드)** 다. 아직 도메인 코드는 없고 `global/` 횡단 인프라만 존재한다. 본 문서는 (a) **이미 있는 인프라의 실제 컨벤션**과 (b) **앞으로 도메인을 추가할 때의 표준**을 함께 정의한다. "표준"이라고 적힌 항목은 아직 코드에 없더라도 신규 코드가 따라야 할 규칙이다. **존재하지 않는 인프라(보안/권한 체계, Redis, 메시지 큐 등)를 임의로 만들지 말고, 필요하면 먼저 묻는다.**

---

## 1. 프로젝트 개요

**서비스**: nuro — AI 피부 진단 웹 서비스의 백엔드.
**핵심 기능**: 사용자가 업로드한 피부 사진을 **비전 LLM**에 전달해 **고정된 JSON 스키마**로 구조화된 진단 결과를 반환한다.
**MVP 범위**: `사진 업로드 → 이미지 저장·리사이즈 → 비전 LLM 호출(고정 JSON 스키마) → 구조화된 진단 결과 반환` 의 end-to-end 동작.
**확장 예정(현재 범위 아님)**: 성분 카드 · 제품 추천 · 루틴 형성 등. 지금은 진단 기능에 집중한다.

**도메인 코드 위치**: 진단 도메인은 `com.nuro.server.diagnosis` 같은 패키지로 추가한다(§5).

---

## 2. 기술 스택 (변경 시 주의)

- **Java 17**(Gradle toolchain), **Spring Boot 3.5.14**, **Gradle 8.14.3 (Groovy DSL)**. 베이스 패키지: `com.nuro.server`
- **Spring AI 1.1.0**(BOM `org.springframework.ai:spring-ai-bom:1.1.0`) — 멀티모달 ChatClient.
  - **기본 모델**: Gemini 2.5 Flash (`spring-ai-starter-model-google-genai`)
  - **승급 모델**: Claude Sonnet 4.6 (`spring-ai-starter-model-anthropic`)
  - 모델 전환은 코드 수정이 아니라 **`spring.ai.model.chat` 설정값**(`google-genai` / `anthropic` / `none`)으로 한다.
- **DB**: PostgreSQL(운영) / H2(로컬·테스트, `MODE=PostgreSQL`). → **H2에서도 PostgreSQL 방언으로 동작하므로 SQL/DDL은 PostgreSQL 기준으로 작성.**
- **기타**: Spring Web + **WebFlux**(Spring AI/WebClient용), Spring Data JPA, Bean Validation, Spring AOP, Actuator, **AWS SDK v2 S3 2.39.2**(이미지 저장용, 아직 config 미구성), springdoc-openapi 2.8.9(Swagger UI), Lombok.

**아직 없는 것(임의 도입 금지, 필요 시 먼저 논의)**: Spring Security / JWT / 권한 체계, Redis, 메시지 큐, Spotless 포맷터, Testcontainers, 별도 integrationTest sourceSet, Flyway/Liquibase 마이그레이션.

---

## 3. 빌드 / 실행 / 테스트

```bash
# 빌드 + 전체 테스트 (CI와 동일)
./gradlew clean build

# 테스트만
./gradlew test

# AI 키 없이 서버만 띄우기 (로컬 기본 확인용)
SPRING_AI_MODEL_CHAT=none ./gradlew bootRun
# Windows PowerShell: $env:SPRING_AI_MODEL_CHAT="none"; .\gradlew.bat bootRun

# 운영 프로파일 실행
SPRING_PROFILES_ACTIVE=prod DB_URL=... DB_USERNAME=... DB_PASSWORD=... GEMINI_API_KEY=... ./gradlew bootRun

# 실행 JAR
./gradlew clean bootJar   # build/libs/server-0.0.1-SNAPSHOT.jar
```

**확인 endpoint**(로컬): 헬스 `http://localhost:8080/actuator/health` · API 문서 `http://localhost:8080/swagger-ui.html` · H2 콘솔 `http://localhost:8080/h2-console`(JDBC `jdbc:h2:mem:nuro`, user `sa`, pw 공란).

**코드 수정 후 검증 체크리스트**: ① `./gradlew test` 통과 ② AI를 건드렸으면 `SPRING_AI_MODEL_CHAT=none`으로 서버가 뜨는지 확인(키 없이도 부팅돼야 함) ③ 새 도메인이면 해당 단위 테스트 작성·실행.

> 포맷터(Spotless)는 없다. 기존 코드 스타일(4-space indent, import 정리, Lombok)을 눈으로 맞춘다. 임의로 포맷터를 도입하지 않는다.

---

## 4. 프로파일 / 환경 매트릭스

| 항목 | 로컬(`local`, 기본) | 테스트(`test` resources) | 운영(`prod`) |
|---|---|---|---|
| DB | H2 in-memory (`jdbc:h2:mem:nuro;MODE=PostgreSQL`) | H2 in-memory (`nuro-test`) | PostgreSQL (`${DB_URL}`) |
| `jpa.hibernate.ddl-auto` | `create-drop` | `create-drop` | **`validate`** (스키마 자동 변경 금지) |
| `show-sql` | `true` | — | `false` |
| `spring.ai.model.chat` | `google-genai` (Gemini) | `none` (AI 비활성) | `google-genai` (기본) |
| H2 콘솔 | ON (`/h2-console`) | — | — |
| Actuator 노출 | `health, info` | `health, info` | `health, info` |
| AI 키 주입 | `GEMINI_API_KEY` 환경변수 | dummy 키 (`test-dummy-key`) | `GEMINI_API_KEY` / `ANTHROPIC_API_KEY` 환경변수 |

- 설정 파일: `src/main/resources/application.yml`(`---`로 프로파일 분리, default=`local`), 테스트 전용 override는 `src/test/resources/application.yml`.
- **운영은 `ddl-auto: validate`** → 엔티티/컬럼 추가가 자동 반영 안 된다. 스키마 변경 시 **PostgreSQL DDL을 별도로 준비**해서 PR에 첨부하고, 사람이 운영 DB에 수동 적용(§16, §17).
- **시크릿(API 키·DB 비번)은 환경변수로만**. yml/코드/로그에 실제 값 하드코딩 금지. `application-local.yml`·`application-secret.*`·`.env*`는 `.gitignore` 처리됨.

---

## 5. 패키지 / 디렉토리 컨벤션

베이스: `com.nuro.server`

```
src/main/java/com/nuro/server/
├── NuroApplication.java
├── global/                      ← 횡단 관심사 (이미 존재)
│   ├── aop/                     # ExecutionTimeAspect(컨트롤러 실행시간), LoggingAspect(서비스 로깅)
│   ├── config/                  # AsyncConfig, JpaAuditingConfig, swagger/SwaggerConfig
│   ├── entity/                  # BaseEntity (createdAt/updatedAt/deletedAt + soft delete)
│   ├── exception/               # ErrorCase(interface), GlobalErrorCase, ApplicationException, GlobalExceptionHandler
│   └── response/                # CommonResponse<T> (표준 응답 래퍼)
└── {domain}/                    ← 도메인 단위 (예: diagnosis) — 신규 추가
```

### 도메인 패키지 표준 구조

```
{domain}/                        ← 예: diagnosis
├── controller/                  # *Controller
├── service/                     # *Service
├── repository/                  # *Repository (Spring Data JPA)
├── entity/                      # 엔티티 (BaseEntity 상속)
├── dto/
│   ├── request/                 # {Resource}{Action}Request
│   └── response/                # {Resource}{Action}Response
├── exception/                   # {Domain}ErrorCase (implements ErrorCase) — 도메인 에러 enum
└── (선택) enums/, util/, client/  # 도메인 enum, 전용 유틸, 외부연동
```

**패키지명 룰**: 한 단어는 그대로(`diagnosis`), 두 단어 이상은 `snake_case`. **`global/`은 영향이 크므로 수정 시 신중하게** — 수정이 도메인 경계를 넘는지 항상 확인한다.

---

## 6. 엔티티 / JPA 룰

### 6.1 기본 골격 (필수)
```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diagnosis")
public class Diagnosis extends BaseEntity {        // ← 모든 엔티티는 BaseEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Diagnosis(...) { ... }                  // private 생성자
    public static Diagnosis create(...) { ... }     // 정적 팩토리
}
```

- **모든 도메인 엔티티는 `global/entity/BaseEntity` 상속** → `createdAt`/`updatedAt`/`deletedAt`와 `softDelete()`/`restore()`/`isDeleted()`를 공통 제공. JPA Auditing(`@CreatedDate`/`@LastModifiedDate`)은 `JpaAuditingConfig`로 이미 활성.
- **`@NoArgsConstructor(PROTECTED)` + private 생성자 + `public static create(...)` 정적 팩토리**가 표준. public 생성자 금지.
- **Setter 금지.** 상태 변경은 의미 있는 도메인 메서드(`updateXxx`, `markXxx`, `complete`, `cancel` 등)로.

### 6.2 Soft Delete
- `BaseEntity.softDelete()`로 `deletedAt`을 채워 논리 삭제하는 게 **기본 방침**. 물리 삭제는 정당한 사유가 있을 때만.
- 조회 시 삭제된 행을 제외해야 하면 repository 메서드/쿼리에서 `deletedAt IS NULL` 조건을 **명시**한다(전역 `@Where` 필터는 아직 도입 안 함 — 도입하려면 먼저 논의).

### 6.3 컬럼 / 관계 매핑
- **Enum 컬럼**: `@Enumerated(EnumType.STRING)` + `@Column(nullable = false, length = N)`. **ORDINAL 금지.**
- **긴 텍스트(LLM 원문/JSON 등)**: `@Column(columnDefinition = "TEXT")`.
- **관계는 `@ManyToOne(fetch = FetchType.LAZY)` 기본.** EAGER 금지. `@JoinColumn(name = "xxx_id", nullable = ...)` 명시.
- `precision/scale` 등 정밀 컬럼은 의도를 `@Column`에 명시.

### 6.4 도메인 메서드 패턴
- 상태 변경: `update*`, `mark*`, 토글: `toggle*`, 생명주기: `complete()`, `fail()`, `cancel()`.
- 불변식 위반 검증은 도메인 메서드 안에서 `ApplicationException`(도메인 `ErrorCase`)으로 던진다.

---

## 7. 에러 / 예외 룰 (이 프로젝트의 핵심 컨벤션)

> nuro는 **`ErrorCase` 인터페이스 + 도메인별 enum + `ApplicationException`** 구조를 따른다.

### 7.1 ErrorCase 인터페이스
`global/exception/ErrorCase.java`:
```java
public interface ErrorCase {
    Integer getHttpStatusCode();   // HTTP 상태 (400, 404, 500 ...)
    Integer getErrorCode();        // 비즈니스 에러 코드 (4자리 권장)
    String getMessage();           // 사용자 노출 메시지 (한글 OK)
}
```

### 7.2 도메인별 ErrorCase enum (표준)
**도메인 에러는 단일 전역 enum에 모으지 않는다. 각 도메인 패키지에 자기 enum을 만든다.**
```java
// domain/diagnosis/exception/DiagnosisErrorCase.java
@Getter
@RequiredArgsConstructor
public enum DiagnosisErrorCase implements ErrorCase {

    IMAGE_TOO_LARGE(400, 4001, "이미지 용량이 허용 범위를 초과했습니다."),
    UNSUPPORTED_IMAGE_TYPE(400, 4002, "지원하지 않는 이미지 형식입니다."),
    DIAGNOSIS_NOT_FOUND(404, 4040, "진단 결과를 찾을 수 없습니다."),
    LLM_RESPONSE_INVALID(502, 5021, "AI 응답을 해석하지 못했습니다."),
    LLM_CALL_FAILED(502, 5020, "AI 호출에 실패했습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}
```
- 전역/공통 에러는 `global/exception/GlobalErrorCase` 사용(`INVALID_INPUT`, `RESOURCE_NOT_FOUND`, `METHOD_NOT_ALLOWED`, `INTERNAL_SERVER_ERROR`).
- **`errorCode`(4자리) 도메인 간 충돌 주의** — 새 enum 만들 때 기존 코드 대역과 겹치지 않게. 도메인별 천 단위 대역을 권장(예: 진단 40xx, 사용자 41xx ...).
- **외부(LLM/S3 등) 장애는 5xx로 표현**(`502`/`504`). 4xx로 가리지 말 것.

### 7.3 예외 던지기 / 잡기
```java
throw new ApplicationException(DiagnosisErrorCase.DIAGNOSIS_NOT_FOUND);
// 또는
throw ApplicationException.from(DiagnosisErrorCase.IMAGE_TOO_LARGE);
// 원인 체이닝
throw new ApplicationException(DiagnosisErrorCase.LLM_CALL_FAILED, e);
```
- `ApplicationException`만 던진다. **별도 예외 클래스를 도메인마다 새로 만들지 않는다**(회사 스타일과 다름) — `ErrorCase` enum 값이 곧 에러 식별자다.
- **`GlobalExceptionHandler`가 이미 모든 예외를 잡아 `CommonResponse`로 변환한다(§8).** `@RestControllerAdvice`에 핸들러를 추가하는 일은 거의 없다. 정말 필요하면 기존 패턴(로깅 → `status(...).body(CommonResponse.error(...))`)을 유지.
- `GlobalExceptionHandler`의 fallback `Exception` 핸들러는 **`/actuator` 요청은 다시 throw**한다(헬스체크가 200을 유지하도록) — 이 동작 건드리지 말 것.

---

## 8. 응답 컨벤션 (CommonResponse 래퍼)

**모든 API는 `global/response/CommonResponse<T>`로 감싼다.** `@JsonInclude(NON_NULL)`이라 null 필드는 직렬화에서 빠진다.

```java
// 성공 + 데이터
return CommonResponse.success(diagnosisResponse);      // {"message":"success","data":{...}}
// 성공 + 데이터 없음
return CommonResponse.success();                       // {"message":"success"}
// 에러는 GlobalExceptionHandler가 자동 생성: {"errorCode":4040,"message":"..."}
```

- 컨트롤러는 보통 `CommonResponse<T>`를 직접 반환하거나, 상태코드 제어가 필요하면 `ResponseEntity<CommonResponse<T>>`로 감싼다.
- 정상 응답은 200 OK 기본, 생성은 201 Created. 에러 상태코드는 **`ErrorCase.getHttpStatusCode()` 값과 정확히 일치**하게.

---

## 9. Controller 룰

### 9.1 골격
```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diagnoses")
@Tag(name = "피부 진단", description = "사진 업로드 → AI 진단")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @Operation(summary = "피부 사진 진단", description = "이미지를 업로드하면 구조화된 진단 결과를 반환합니다. 결과는 참고용입니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<DiagnosisResponse> diagnose(@RequestPart("image") MultipartFile image) {
        return CommonResponse.success(diagnosisService.diagnose(image));
    }
}
```

- **URL prefix**: `/api/v1/...`. 도메인 1차 컨텍스트는 path로(`/api/v1/diagnoses/{id}`).
- **이미지 업로드는 `multipart/form-data` + `MultipartFile`**. 멀티파트 한도는 yml에 설정됨(파일 10MB / 요청 12MB) — 초과 시 핸들러가 잡아 400 처리.
- `@Valid @RequestBody` + Bean Validation 적극 사용(§12).
- **Swagger 필수**: 모든 컨트롤러 메서드에 `@Operation`. 가능하면 `@ApiResponses`로 4xx/5xx도 문서화. `@Tag`는 한글 설명.
- 인증/권한 체계는 **아직 없다.** 권한이 필요한 엔드포인트가 생기면 Spring Security 도입을 **먼저 논의**하고, 임의로 필터/권한 코드를 만들지 않는다.

---

## 10. Service 룰

### 10.1 골격
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)                  // ← 클래스 레벨 readOnly
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final SkinDiagnosisClient skinDiagnosisClient;   // LLM 호출 래퍼 (§13)
    private final ImageStorage imageStorage;                 // S3/로컬 저장 (§14)

    @Transactional                               // ← 쓰기 메서드에만 명시
    public DiagnosisResponse diagnose(MultipartFile image) { ... }
}
```
- 클래스 레벨 `@Transactional(readOnly = true)` + 쓰기 메서드만 `@Transactional` 오버라이드.
- 생성자 주입(`@RequiredArgsConstructor`). `@Autowired` 필드 주입 금지.
- **LLM/S3 같은 느리고 실패 가능한 외부 호출을 트랜잭션 안에서 길게 잡지 말 것.** 가능하면 외부 호출 결과를 받은 뒤 짧은 트랜잭션에서 영속화한다.

### 10.2 AOP 로깅 인지
- `LoggingAspect`가 `com.nuro.server..service..*`의 모든 메서드 진입/종료/예외를 자동 로깅하고, `ExecutionTimeAspect`가 `..controller..*` 실행시간을 로깅한다.
- 따라서 **서비스 메서드 인자에 민감정보/대용량을 그대로 넣으면 로그에 찍힐 수 있다.** `LoggingAspect.safeValue()`가 `MultipartFile`/`byte[]` 등은 요약하지만, 일반 DTO는 `toString()`된다 → **개인정보·원문 이미지 base64 등을 서비스 인자로 노출하지 않도록** 주의(필요하면 `safeValue` 분기 추가를 논의).

### 10.3 비동기
- `@EnableAsync`는 **`global/config/AsyncConfig`에 한 번만** 있다. **다른 곳에 `@EnableAsync` 추가 금지**(중복 시 사일런트 실패 위험).
- 별도 `taskExecutor` 빈은 아직 없다 — 본격적인 비동기 워커가 필요하면 executor 설정을 먼저 논의.

---

## 11. Repository 룰

- Spring Data JPA `JpaRepository<Entity, Long>` 기본.
- 메서드 명명: 파생 쿼리(`findById`, `findByXxx`, `existsByXxx`) 우선, JPA underscore 탐색(`findByDiagnosis_Id`) OK.
- 복잡 쿼리는 `@Query` JPQL. enum 비교는 파라미터 바인딩(`where d.status = :status`).
- N+1은 `@EntityGraph(attributePaths = {...})`로 명시 해결.
- **soft delete를 쓰는 조회는 `deletedAt IS NULL` 조건을 쿼리/메서드에 명시**(§6.2).

---

## 12. DTO 컨벤션

- **위치/명명**: `dto/request/`·`dto/response/` 분리. 요청 `{Resource}{Action}Request`, 응답 `{Resource}{Action}Response`, 공통 요약은 `{Resource}{Property}Dto`.
- **요청 DTO**: `record` 또는 `@Getter @NoArgsConstructor`. Bean Validation(`@NotNull`, `@NotBlank`, `@Size`, `@Min`/`@Max`) 적극 사용, **메시지는 한글**. 검증 실패는 `GlobalExceptionHandler`가 `MethodArgumentNotValidException` → 400 `INVALID_INPUT`으로 자동 변환(첫 번째 위반 메시지를 노출).
- **응답 DTO**: `record` 또는 `@Getter @Builder`. 정적 팩토리 `from(Entity)`/`of(...)` 권장. 엔티티를 컨트롤러로 그대로 노출하지 말 것.
- **LLM 진단 결과 DTO**: 비전 LLM의 **구조화 출력 대상 타입**(§13)과 API 응답 타입을 분리할지/공유할지는 도메인에서 결정하되, 외부 응답에는 면책 문구·필요 메타데이터를 포함한다(§13.4).
- **boolean JSON 키 주의**: Lombok `isXxx()` 게터는 JSON 키에서 `is`가 떨어질 수 있다(`{"new":true}`). 의도가 `isNew`면 `@JsonProperty("isNew")` 명시.

---

## 13. Spring AI / 비전 LLM 통합 (제품 핵심)

> 이 서비스의 본질은 "피부 사진 → 고정 JSON 스키마 진단"이다. 아래는 신규 진단 로직의 표준이다.

### 13.1 모델 추상화 / 전환
- 모델 선택은 **설정값 `spring.ai.model.chat`** 으로만 한다: `google-genai`(기본 Gemini 2.5 Flash) / `anthropic`(승급 Claude Sonnet 4.6) / `none`(AI 비활성, 부팅·로컬 확인용).
- **코드에서 특정 벤더 클라이언트(GeminiChatModel, AnthropicChatModel 등)에 직접 의존하지 말 것.** Spring AI의 **`ChatClient`(또는 `ChatModel`) 추상화**에만 의존해 모델 교체가 설정으로 끝나게 한다.
- 모델 ID/온도/max-tokens 등은 **코드 상수로 박지 말고 yml**(`spring.ai.*.chat.options.*`)에서 받는다. 최신 모델 ID는 README/yml 기준(Gemini `gemini-2.5-flash`, Claude `claude-sonnet-4-6`).

### 13.2 멀티모달 호출 + 구조화 출력 (표준)
이미지 + 프롬프트를 보내고 **고정 타입으로 역직렬화**하는 게 핵심. Spring AI 1.1.x `ChatClient` 패턴:
```java
@Component
@RequiredArgsConstructor
public class SkinDiagnosisClient {

    private final ChatClient.Builder chatClientBuilder;   // 자동 주입 (활성 모델로 바인딩)

    public SkinDiagnosisResult diagnose(byte[] imageBytes, MimeType mimeType) {
        try {
            return chatClientBuilder.build()
                    .prompt()
                    .system(DIAGNOSIS_SYSTEM_PROMPT)          // 역할/JSON 스키마/제약을 명시
                    .user(u -> u.text(DIAGNOSIS_USER_PROMPT)
                                .media(mimeType, new ByteArrayResource(imageBytes)))
                    .call()
                    .entity(SkinDiagnosisResult.class);       // ← 구조화 JSON 자동 매핑
        } catch (Exception e) {
            throw new ApplicationException(DiagnosisErrorCase.LLM_CALL_FAILED, e);
        }
    }
}
```
- **구조화 출력은 `.entity(Type.class)`**(또는 `ParameterizedTypeReference`)로 받는다 — 수동 JSON 파싱 금지. 매핑 실패는 `LLM_RESPONSE_INVALID`(502)로 변환.
- 스키마는 **고정**한다(README의 "고정 JSON 스키마"). 진단 결과 타입 필드를 함부로 바꾸면 프론트/계약이 깨진다 — 변경 시 PR에 명시.
- 시스템 프롬프트에 **"의학적 진단이 아닌 참고용", 출력 분량 제한**을 명시(§13.4 정책과 일치).

### 13.3 비활성 모드(`none`) 대응
- `spring.ai.model.chat=none`이면 ChatClient 빈이 없을 수 있다. **AI 키 없이도 서버가 부팅돼야 한다**(로컬 온보딩 핵심). 진단 외 엔드포인트가 ChatClient 빈 부재로 깨지지 않게 주입을 선택적으로 다루거나(`ObjectProvider`/`@Autowired(required=false)`), 진단 호출 시점에만 검증해 명확한 에러를 던진다.

### 13.4 비용 / 안전 정책 (반드시 지킬 것)
1. **LLM 전송 전 이미지 리사이즈**(긴 변 ~1024px) → 입력 토큰 비용 통제. 원본을 그대로 보내지 말 것.
2. **출력 분량 제한**(`max-output-tokens`/`max-tokens`는 yml에서, 현재 2048) → 출력 토큰 통제.
3. **진단 결과에 "참고용이며 의학적 진단이 아님" 면책 문구 포함.**
4. **API 키는 환경변수**(`GEMINI_API_KEY`/`ANTHROPIC_API_KEY`). 코드/yml/로그에 키나 사용자 이미지 원문을 남기지 않는다.
5. LLM/외부 호출 실패·타임아웃은 5xx 계열 `ErrorCase`로 표현하고, 사용자에겐 일반화된 메시지를 준다(LLM raw 에러를 그대로 노출 금지).

---

## 14. 이미지 업로드 / 저장

- 업로드는 `MultipartFile`(multipart). 한도: 파일 10MB / 요청 12MB(yml). 초과·형식 위반은 `DiagnosisErrorCase`로 검증.
- **저장 추상화**: 저장소(로컬 `/tmp/uploads` ↔ S3)를 인터페이스(`ImageStorage`)로 추상화해 환경별로 구현을 바꾼다. MVP 로컬은 디스크, 운영은 S3(`software.amazon.awssdk:s3`).
  - 로컬 업로드 디렉토리(`uploads/`, `/tmp/uploads/`)는 `.gitignore` 처리됨.
  - S3 config/버킷/자격증명은 아직 없다 → 운영 연동 시 자격증명은 환경변수/IAM Role로, **키 하드코딩 금지**. 도입 전 먼저 논의.
- 저장 전 **리사이즈**(§13.4-1)와 **MIME 타입 검증**을 거친다. LLM에는 리사이즈된 바이트를 전달.

---

## 15. 테스트

> 표준 Spring Boot 테스트 스택(JUnit5 + Mockito + Spring Boot Test)을 쓴다. 테스트는 H2(`MODE=PostgreSQL`)와 `spring.ai.model.chat=none`(test resources) 위에서 돈다.

### 15.1 서비스 단위 테스트
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("DiagnosisService 단위 테스트")
class DiagnosisServiceTest {

    @InjectMocks private DiagnosisService diagnosisService;
    @Mock private DiagnosisRepository diagnosisRepository;
    @Mock private SkinDiagnosisClient skinDiagnosisClient;
    @Mock private ImageStorage imageStorage;
}
```
- **AssertJ만 사용**(`assertThat`, `assertThatThrownBy`). JUnit `assertEquals`/`assertTrue` 금지.
- **BDDMockito만 사용**(`given(...).willReturn(...)`, `then(...).should(...)`). `when().thenReturn()` 금지.
- `@Nested`로 메서드 단위 분할, `@DisplayName`은 `성공 - <조건>` / `실패 - <조건> → <ErrorCase>` 형식.
- private 생성자/정적 팩토리 엔티티는 `mock(...)` 또는 정적 팩토리로 생성. 생성자 직접 호출이 막히면 `ReflectionTestUtils.setField`로 필드 주입(특히 검증용 DTO).

### 15.2 컨트롤러 슬라이스 테스트
```java
@WebMvcTest(controllers = DiagnosisController.class)
@DisplayName("DiagnosisController 슬라이스 테스트")
class DiagnosisControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private DiagnosisService diagnosisService;   // ← @MockBean(deprecated) 금지
}
```
- `@WebMvcTest(controllers = ...)`로 슬라이스 명시. `@MockitoBean`(Spring Boot 3.4+) 사용.
- 응답 검증은 **`CommonResponse` 래퍼 구조 기준**: `jsonPath("$.message").value("success")`, `jsonPath("$.data.id")`, 에러는 `jsonPath("$.errorCode")`.
- HTTP 상태는 **`ErrorCase.getHttpStatusCode()`와 정확히 일치**하는지 확인.
- 멀티파트 엔드포인트는 `multipart(...).file(new MockMultipartFile(...))`로 검증.
- `.andDo(print())`로 실패 디버깅을 돕는다.

### 15.3 테스트 데이터 (Fixture)
- **검증/생성용 DTO는 도메인별 Fixture 클래스로 만든다.** 위치: `src/test/java/com/nuro/server/{domain}/fixture/XxxRequestFixture.java`. 요청 DTO가 private 필드 + Bean Validation이라 테스트마다 새로 조립하면 깨지기 쉬우므로 한 곳에 모은다.
  - private 필드는 **`ReflectionTestUtils.setField(req, "field", value)`로 주입**한다. **테스트 편의용 빌더/세터를 프로덕션 DTO에 추가하지 말 것.**
  - 자주 쓰는 시나리오는 **`defaultXxxRequest()` 헬퍼**로 제공해 테스트 가독성을 높인다. 잘못된 케이스용 헬퍼(`invalidXxxRequest()`)도 같이 둔다.
  - 정적 메서드만(인스턴스화 금지).
- **같은 mock 엔티티 트리를 3개 이상 테스트가 공유하면 Entity Fixture로 분리**(`{domain}/fixture/XxxEntityFixture.java`). 그 전까진 각 테스트의 `@BeforeEach`에서 만든다.

### 15.4 의무
- **신규 도메인 최소**: 서비스 단위 테스트 1 + 컨트롤러 슬라이스 테스트 1.
- **LLM 클라이언트는 모킹**해서 결정론적으로 테스트(실 LLM 호출 금지 — 비용·불안정). 구조화 매핑 실패/외부 장애 분기(`LLM_RESPONSE_INVALID`/`LLM_CALL_FAILED`)도 케이스로 작성.
- `@SpringBootTest`는 정말 필요할 때만(느림). 슬라이스로 가능한 건 슬라이스로.

---

## 16. 알려진 함정 / 금지사항

1. **운영 `ddl-auto: validate`** — 엔티티/컬럼 변경이 자동 반영 안 된다. 스키마 변경 시 **PostgreSQL DDL을 별도로 준비**해 PR 첨부 + 사람이 수동 적용. (validate라 스키마 불일치면 부팅 실패.)
2. **SQL/DDL은 PostgreSQL 기준.** 로컬·테스트가 H2(`MODE=PostgreSQL`)라 H2 전용 문법에 의존하지 말 것.
3. **`@EnableAsync` 중복 금지** — `AsyncConfig`에만.
4. **API 키 없이도 서버가 떠야 한다** — `spring.ai.model.chat=none` 경로를 깨지 말 것(로컬 온보딩 핵심).
5. **시크릿 하드코딩 금지** — API 키·DB 비번·사용자 이미지 원문을 코드/yml/로그에 남기지 않는다. `LoggingAspect`가 서비스 인자를 로깅한다는 점 인지(§10.2).
6. **`GlobalExceptionHandler`의 `/actuator` 재throw 동작** 유지 — 헬스체크 200 보장용.
7. **`CommonResponse` 래핑 일관성** — 어떤 엔드포인트는 래핑, 어떤 건 raw로 섞지 말 것. 전부 `CommonResponse`.
8. **LLM 구조화 출력 스키마 변경은 계약 변경** — 진단 결과 타입 필드 변경 시 PR에 명시하고 프론트와 합의.
9. **벤더 종속 코드 금지** — 항상 Spring AI 추상화(`ChatClient`/`ChatModel`)로. 모델 전환은 설정으로.
10. **이미지 리사이즈 없이 원본을 LLM에 전송 금지**(토큰 비용).
11. **악성 코드 / 취약점 코드 작성 거부** — 보안 연구 명목이라도 금지.
12. **JAR 이름은 `version`에 결합**(`server-0.0.1-SNAPSHOT.jar`). 배포 스크립트/Dockerfile이 생기면 버전 동기화 의식(현재는 둘 다 없음).

---

## 17. 신규 도메인 추가 체크리스트

신규 도메인 `xxx`를 추가할 때:

- [ ] 패키지: 한 단어면 그대로, 두 단어 이상이면 `snake_case`. `com.nuro.server.xxx`.
- [ ] 표준 구조(`controller/`, `service/`, `repository/`, `entity/`, `dto/request/`, `dto/response/`, `exception/`).
- [ ] 엔티티: `BaseEntity` 상속, `@NoArgsConstructor(PROTECTED)` + private 생성자 + `public static create(...)`. soft delete 활용 여부 결정.
- [ ] 에러: `XxxErrorCase implements ErrorCase` enum 생성, `errorCode` 대역이 기존과 충돌하지 않게(§7.2). 던지기는 `ApplicationException`.
- [ ] 컨트롤러: `/api/v1/...`, `CommonResponse<T>` 반환, `@Operation`/`@Tag` 부착, Bean Validation.
- [ ] 서비스: 클래스 `@Transactional(readOnly = true)` + 쓰기 메서드만 `@Transactional`. 외부 호출은 트랜잭션 밖/짧게.
- [ ] (LLM 연동이면) §13 — Spring AI 추상화, `.entity(...)` 구조화 출력, 비용/면책 정책, 모킹 테스트.
- [ ] (이미지 다루면) §14 — multipart 한도, 리사이즈, MIME 검증, 저장 추상화.
- [ ] 응답에 민감정보 노출 검토. AOP 로깅에 민감정보가 찍히지 않는지 확인.
- [ ] 스키마 변경 동반 시 PostgreSQL DDL을 PR에 첨부(운영 `validate`).
- [ ] 새 yml 프로퍼티가 필요하면 local/test/prod(환경변수) 모두 반영.
- [ ] 테스트: 서비스 단위 + 컨트롤러 슬라이스(§15). 검증용 DTO는 `fixture/` Fixture 클래스 + `ReflectionTestUtils`(§15.3). LLM/외부 호출은 모킹.

---

## 18. Claude Code 작업 지침 (셀프 룰)

1. **수정 전 영향 범위 확인.** `global/*` 수정은 영향이 크므로 신중히. 한 파일 수정이 도메인 경계를 넘는지 본다.
2. **존재하지 않는 인프라를 발명하지 않는다.** 보안/권한 체계, Redis, 큐, 포맷터, 마이그레이션 툴 등은 아직 없다 — 필요하면 **먼저 묻는다**.
3. **이 프로젝트의 primitive를 쓴다.** 에러는 `ErrorCase`+`ApplicationException`, 응답은 `CommonResponse`, 엔티티는 `BaseEntity`. (회사 코드의 `BaseException`/단일 `ErrorCode` enum/`@PreAuthorize` 등을 옮겨오지 말 것.)
4. **테스트 동반.** 신규 코드는 §15 표준대로. LLM/외부 호출은 모킹.
5. **시크릿·민감정보 보호.** §16-5.
6. **운영 영향 작업(스키마 변경, prod yml, 배포 설정)은 명시 승인 후.** 운영 DDL은 사람이 수동 실행.
7. **한글 OK.** 비즈니스 에러 메시지·Swagger 설명·DisplayName은 자연스러운 한글로.
8. **모호하면 추측보다 질문.** 룰 충돌이나 패턴이 불명확하면 한 번 묻는 게 낫다.
9. **브랜치 전략**: `main` ← `develop` ← `feature/*`. 커밋/푸시는 사용자가 요청할 때만.
```