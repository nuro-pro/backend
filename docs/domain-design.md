# nuro 도메인 설계 & 스켈레톤 코드 구성 가이드

> 피그마 확정본 기반 도메인 초안. **현재는 스켈레톤만** 있고 비즈니스 로직은 비어 있음
---

## 1. 화면 → 도메인 매핑 (피그마 플로우)

| 피그마 화면 | 동작 | 담당 도메인 |
|---|---|---|
| 인트로 | 온보딩 안내 | (프론트) |
| 닉네임/나이 입력 | 익명 사용자 생성 | **user** |
| 얼굴 촬영/확인 | 이미지 업로드 | **diagnosis** |
| 설문 1~12 (4지선다) | 문항 조회 / 응답 제출 | **survey** |
| 이미지 분석중… | 비전 LLM 호출 | **diagnosis** |
| 결과 - 피부타입/6개 지표/종합점수/항목별 | 진단 결과 | **diagnosis** |
| 결과 - 나와 잘 맞는 성분 | 성분 추천 | **ingredient** |
| 결과 - 추천 스킨케어 루틴 | 루틴 추천 | **routine**  |
| 결과 - 문자 전송 | 결과 SMS | **notification** |

---

## 2. 도메인 목록 & 책임

| 도메인 | 패키지 | 핵심 책임 | errorCode 대역 |
|---|---|---|---|
| user | `com.nuro.server.user` | 익명 사용자(닉네임/나이) | 41xx |
| survey | `com.nuro.server.survey` | 설문 문항/응답 | 42xx |
| diagnosis | `com.nuro.server.diagnosis` | **핵심**: 사진→리사이즈→저장→LLM→구조화 결과 | 40xx |
| ingredient | `com.nuro.server.ingredient` | 진단 기반 추천 성분 *(확장 예정)* | 43xx |
| routine | `com.nuro.server.routine` | 진단 기반 추천 루틴 *(확장 예정)* | 44xx |
| notification | `com.nuro.server.notification` | 결과 문자(SMS) 전송 *(인터페이스+no-op)* | 45xx |

> global 공통 에러(`GlobalErrorCase`)는 4000/4040/4050/5000을 사용중 

---

## 3. 엔드포인트 맵 (초안 -> 알아서 자유롭게 변경)

| Method | Path | 설명                                        |
|---|---|-------------------------------------------|
| POST | `/api/v1/users` | 사용자 등록(닉네임/나이) |
| GET | `/api/v1/users/{userId}` | 사용자 조회 |
| GET | `/api/v1/surveys/questions` | 설문 문항 목록 |
| POST | `/api/v1/surveys/answers` | 설문 응답 일괄 제출 |
| POST | `/api/v1/diagnoses` | 사진 업로드 → 진단 (multipart, `userId` + `image`) |
| GET | `/api/v1/diagnoses/{diagnosisId}` | 진단 결과 조회  |
| GET | `/api/v1/ingredients/recommendations?diagnosisId=` | 추천 성분  |
| GET | `/api/v1/routines/recommendations?diagnosisId=` | 추천 루틴   |
| POST | `/api/v1/notifications/diagnosis-result` | 결과 문자 전송  |

---

## 4. 도메인 간 관계 

- **다른 도메인 엔티티는 직접 `@ManyToOne` 하지 않고 `Long xxxId`로 느슨하게 참조**한다
  - 예: `Diagnosis.userId`, `SurveyAnswer.userId`, `Routine.diagnosisId`.
- **같은 도메인 내부는 `@ManyToOne(LAZY)` / `@OneToMany`** 
  - 예: `Diagnosis ↔ DiagnosisMetric`, `Routine ↔ RoutineStep`.

```
user ──(userId)──> diagnosis ──(diagnosisId)──> ingredient / routine / notification
survey ──(userId)──> (진단 입력 보조)
```

---

## 5. 핵심 플로우 (diagnosis, 구현 시 순서)

`DiagnosisService.diagnose(userId, image)`:
1. MIME/용량 검증 → `UNSUPPORTED_IMAGE_TYPE` / `IMAGE_TOO_LARGE`
2. `ImageResizer.resize(...)` — 긴 변 ~1024px 
3. `ImageStorage.store(...)` → `imageUrl`
4. `Diagnosis.start(userId, imageUrl)` 저장 (status=ANALYZING)
5. `SkinDiagnosisClient.diagnose(bytes, mimeType)`
6. `diagnosis.complete(...)` 또는 실패 시 `fail()` → 저장
7. `DiagnosisResponse.from(...)` 반환

### LLM 연동 규칙 
- 벤더 클라이언트 직접 의존 금지 → Spring AI `ChatClient` 추상화만. 모델 전환은 `spring.ai.model.chat` 설정
- `none` 모드에서도 부팅돼야 함 → `SkinDiagnosisClient`는 `ObjectProvider<ChatClient.Builder>`로 선택 주입
- 구조화 출력은 `.entity(SkinDiagnosisResult.class)` (수동 파싱 금지). 매핑 실패=`LLM_RESPONSE_INVALID`, 호출 실패=`LLM_CALL_FAILED`
- **`SkinDiagnosisResult` 필드 변경 = LLM 출력 계약 변경** → 시스템 프롬프트 변경 필요 
- 결과에 "참고용이며 의학적 진단이 아님" 면책 문구 포함.

---

## 6. 남은 작업(구현 단계 TODO)

- [ ] 각 service/엔티티의 `UnsupportedOperationException` 스탭을 실제 로직으로 구현
- [ ] **테스트**: 도메인별 서비스 단위 + 컨트롤러 슬라이스. 검증용 DTO는 `fixture/` + `ReflectionTestUtils` LLM/외부 호출은 모킹
      → 스켈레톤 단계라 스텁 테스트는 의미가 없어 **미작성**. 실제 구현과 함께 추가할 것
- [ ] 진단 지표 6종(수분/유분/주름/색소/모공/트러블 등) 확정 → 시스템 프롬프트와 일치
- [ ] 설문 문항/선택지 콘텐츠 확정 → 시드 데이터 또는 운영 DDL
- [ ] 이미지 저장: 로컬(`LocalImageStorage`) 구현, 운영 S3 구현 도입 예정 
- [ ] SMS: 실제 벤더(NCP SENS/Twilio 등) 구현체 추가는 **추후 구현**. 현재 `NoOpNotificationSender`
- [ ] **운영 DDL**: 신규 테이블(users, survey_question, survey_question_choice, survey_answer, diagnosis, diagnosis_metric, ingredient, routine, routine_step) PostgreSQL DDL 
- [ ] 성분/루틴 추천 방식 결정: 규칙 기반 vs LLM 추천
- [ ] 인증/권한이 필요해지면 Spring Security 도입 (현재 익명)