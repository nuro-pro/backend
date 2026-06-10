# nuro-be

AI 피부 진단 웹 서비스 **backend** (Spring Boot, Java 17).

사용자가 업로드한 피부 사진을 비전 LLM에 전달해 구조화된(JSON) 피부 진단 결과를 반환하는 것이 핵심 기능이다.

## 개발 범위 (MVP)

`사진 업로드 → 이미지 저장·리사이즈 → 비전 LLM 호출(고정 JSON 스키마) → 구조화된 진단 결과 반환`

전체 서비스 그림(성분 카드 · 제품 추천 · 루틴 형성 등)은 추후 확장 대상이며, 현재 백엔드 범위는 진단 기능 end-to-end 동작까지다.

## 기술 스택

| 구분 | 선택 | 비고 |
|---|---|---|
| Language | Java 17 | toolchain |
| Framework | Spring Boot 3.5.14 | Spring AI 1.1.x와 호환 |
| Build | Gradle 8.14.3 |  |
| AI | Spring AI 1.1.0 (멀티모달) | 모델 교체는 `spring.ai.model.chat` 설정 |
| 기본 모델 | Gemini 2.5 Flash | `google-genai` starter  |
| 승급 모델 | Claude Sonnet 4.6 | `anthropic` starter — `spring.ai.model.chat=anthropic` 로 전환 |
| DB | PostgreSQL (운영) / H2 (로컬·테스트) | |
| 문서 | springdoc-openapi (Swagger UI) | |
| 운영 | Spring Actuator | |

## 프로젝트 구조

```
src/main/java/com/nuro/server
├── NuroApplication.java
└── global
    ├── aop/            ExecutionTimeAspect, LoggingAspect
    ├── config/         JpaAuditingConfig, AsyncConfig, swagger/SwaggerConfig
    ├── entity/         BaseEntity 
    ├── exception/      ErrorCase, ApplicationException, GlobalErrorCase, GlobalExceptionHandler
    └── response/       CommonResponse (표준 응답 래퍼)
```

도메인 기능(진단)은 추후 `com.nuro.server.diagnosis` 등으로 추가한다.
도메인별 에러는 각 도메인에 `XxxErrorCase implements ErrorCase` enum으로 관리한다.

## 환경 변수 

| 변수 | 필수 | 설명 |
|---|---|---|
| `GEMINI_API_KEY` | ✅ | Google AI Studio API 키 (기본 모델) |
| `ANTHROPIC_API_KEY` | 선택 | Claude 승급 시에만 필요 |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | prod | PostgreSQL 접속 정보 |

## 실행

```bash
# 로컬 (기본 프로파일 = local, H2 인메모리)
export GEMINI_API_KEY=...        
./gradlew bootRun

# 운영 (PostgreSQL)
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://localhost:5432/nuro
export DB_USERNAME=... DB_PASSWORD=...
export GEMINI_API_KEY=...
./gradlew bootRun
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 콘솔(local): `http://localhost:8080/h2-console`
- Health: `http://localhost:8080/actuator/health`

```bash
./gradlew build      # 컴파일 + 테스트
./gradlew test       # 테스트만
```

## 정책

- LLM 전송 전 이미지 리사이즈(긴 변 ~1024px)로 토큰 비용 통제
- 출력 분량 제한으로 출력 토큰 통제
- 진단 결과에 "참고용이며 의학적 진단이 아님" 면책 문구 포함
- 비밀값(API 키 등)은 환경변수로 관리, 코드에 하드코딩 금지

## 브랜치 전략

`main` ← `develop` ← `feature/*` (현재 초기세팅: `feature/init`)