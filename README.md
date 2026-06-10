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

## 🚀 로컬 개발 환경 세팅 

> 아래 순서를 **그대로** 따라가면 서버가 뜹니다. 막히면 맨 아래 [트러블슈팅](#-트러블슈팅) 참고 
> 로컬은 기본적으로 메모리 DB(H2)로 설정 

### 0. 필요한 프로그램 설치

| 프로그램 | 확인 명령 | 설치 |
|---|---|---|
| **JDK 17** | `java -version` (17 이상이면 OK) | macOS: `brew install --cask temurin@17` / Windows: [adoptium.net](https://adoptium.net) 에서 Temurin 17 다운로드 |
| **Git** | `git --version` | macOS: `brew install git` / Windows: [git-scm.com](https://git-scm.com) |
| **IntelliJ IDEA** | — | [jetbrains.com/idea](https://www.jetbrains.com/idea/)  |

> JDK가 17이 아니어도 빌드는 됩니다(Gradle이 자동으로 17을 받아옴). 하지만 헷갈리니 처음엔 17 설치를 권장.

### 1. 레포 클론

```bash
git clone https://github.com/nuro-pro/backend.git nuro-be
cd nuro-be
```

### 2. IntelliJ에서 "Gradle 프로젝트로" 열기 

1. IntelliJ → **File → Open** → 방금 받은 `nuro-be` **폴더** 선택 → Open
2. **"Trust Project"** 가 뜨면 **Trust** 클릭
3. 우하단에 *"Importing Gradle project…"* 가 보이고, 끝날 때까지 기다리기 
4. import가 끝나면 `NuroApplication` 의 `main()` 옆에 **초록색 ▶** 으로 서버 실행시켜서 뜨는지 확인 

> ▶ 가 안 보이면 → 아직 Gradle 프로젝트로 인식 안 된 것. 왼쪽 트리에서 `build.gradle` **우클릭 → Link Gradle Project**. 

### 3. (선택) Gradle JVM을 17로 지정

`Settings → Build, Execution, Deployment → Build Tools → Gradle` → **Gradle JVM** 을 17로.
목록에 17이 없으면 그 드롭다운에서 **Download JDK → Temurin 17** 선택.

### 4. 서버 실행

처음엔 **AI 키 없이** 서버만 띄워보기 (AI 호출 끄기). 두 방법 중 하나:

**방법 A — 터미널**
```bash
# macOS / Linux
SPRING_AI_MODEL_CHAT=none ./gradlew bootRun

# Windows (PowerShell)
$env:SPRING_AI_MODEL_CHAT="none"; .\gradlew.bat bootRun
```

**방법 B — IntelliJ ▶ 버튼**
1. `NuroApplication` 옆 ▶ 드롭다운 → **Edit Configurations**
2. **Environment variables** 칸에 `SPRING_AI_MODEL_CHAT=none` 입력
3. ▶ 클릭

> 콘솔에 `Started NuroApplication in ... seconds` 가 보이면 성공

### 5. 잘 떴는지 확인

브라우저에서:
- ✅ (헬스체크) http://localhost:8080/actuator/health → `{"status":"UP"}`
- 📖 (API 문서) http://localhost:8080/swagger-ui.html
- 🗄️ (메모리 DB 콘솔) http://localhost:8080/h2-console — JDBC URL: `jdbc:h2:mem:nuro`, User: `sa`, Password: 공란

---

### 🛠 트러블슈팅

| 증상 | 원인 / 해결 |
|---|---|
| `main()` 옆에 **▶가 안 보임** | Gradle 프로젝트로 인식 안 됨 → `build.gradle` 우클릭 → **Link Gradle Project**. 안 되면 File → Close Project 후 폴더 다시 Open |
| **`Unable to access jarfile ... gradle-wrapper.jar`** | 클론이 덜 됐을 수 있음 → `git pull` 후 `gradle/wrapper/gradle-wrapper.jar` 존재 확인 |
| **`No matching toolchain` / JDK 17 못 찾음** | Settings → Gradle → Gradle JVM 을 17로 지정 (또는 Download JDK → Temurin 17) |
| **`Could not resolve placeholder 'GEMINI_API_KEY'`** | 환경변수 없이 그냥 실행함 → 실행 시 `SPRING_AI_MODEL_CHAT=none` 을 붙이거나 `GEMINI_API_KEY` 설정 |
| **`Port 8080 ... already in use`** | 8080을 쓰는 다른 프로세스가 있음 → 그 프로세스 종료, 또는 `application.yml` 의 `server.port` 변경 |
| 빌드만 확인하고 싶음 | `./gradlew build` (컴파일 + 테스트) / `./gradlew test` (테스트만) |

## 환경 변수

| 변수 | 필수 | 설명 |
|---|---|---|
| `GEMINI_API_KEY` | ✅(AI 호출 시) | Google AI Studio API 키 (기본 모델). 서버만 띄울 땐 `SPRING_AI_MODEL_CHAT=none` 으로 생략 가능 |
| `ANTHROPIC_API_KEY` | 선택 | Claude 승급 시에만 필요 |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | prod | PostgreSQL 접속 정보 (운영 프로파일) |

## 운영(prod) 실행

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://localhost:5432/nuro
export DB_USERNAME=... DB_PASSWORD=...
export GEMINI_API_KEY=...
./gradlew bootRun
```

## 정책

- LLM 전송 전 이미지 리사이즈(긴 변 ~1024px)로 토큰 비용 통제
- 출력 분량 제한으로 출력 토큰 통제
- 진단 결과에 "참고용이며 의학적 진단이 아님" 면책 문구 포함
- 비밀값(API 키 등)은 환경변수로 관리, 코드에 하드코딩 금지

## 브랜치 전략

`main` ← `develop` ← `feature/*` (현재 초기세팅: `feature/init`)