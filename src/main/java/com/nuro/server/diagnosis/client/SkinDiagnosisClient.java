package com.nuro.server.diagnosis.client;

import com.nuro.server.diagnosis.dto.request.DiagnosisRequest;
import com.nuro.server.diagnosis.exception.DiagnosisErrorCase;
import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.ingredient.enums.Ingredients;
import com.nuro.server.survey.entity.SurveyAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 비전 LLM 호출 래퍼
 * 모델 전환은 코드가 아니라 설정({@code spring.ai.model.chat})으로 한다
 */
@Component
@RequiredArgsConstructor
public class SkinDiagnosisClient {

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

    /**
     * 리사이즈된 이미지 + 프롬프트를 전송해 고정 스키마로 역직렬화
     */
    public SkinDiagnosisResult diagnose(byte[] imageBytes,
                                        MimeType mimeType,
                                        String surveyText,
                                        String ingredientNames) {
        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        if (builder == null) {
            // AI 비활성(none) 또는 모델 미구성
            throw new ApplicationException(DiagnosisErrorCase.LLM_CALL_FAILED);
        }

        String prompt = """
                당신은 전문 피부과 AI 분석가입니다.
                아래 사용자 정보와 피부 사진을 바탕으로 피부를 분석해주세요.
                [사용자 설문 응답]
                %s
                
                [분석 기준]
                - 수분: 피부의 수분 보유량과 속건조 여부
                - 주름: 잔주름과 탄력 상태
                - 색소: 색소침착, 칙칙함, 미백 필요도
                - 모공: 모공 크기와 피지 분비 상태
                - 민감: 외부 자극에 대한 피부 반응성
                - 유분: 피지 분비량과 번들거림 정도
                
                반드시 아래 JSON 형식으로만 응답하세요.
                {
                  "skinType": "피부 타입 (예: 건성, 지성, 복합성, 민감성, 중성)",
                  "skinAge": 피부나이 숫자만,
                  "totalScore": 종합점수 0~100 숫자만,
                  "totalDesc": "종합 점수에 대한 한 줄 설명",
                  "summary": "피부 상태 전반에 대한 2~3문장 설명",
                  "metrics": [
                    {"name": "수분", "score": 0~100 숫자만},
                    {"name": "주름", "score": 0~100 숫자만},
                    {"name": "색소", "score": 0~100 숫자만},
                    {"name": "모공", "score": 0~100 숫자만},
                    {"name": "민감", "score": 0~100 숫자만},
                    {"name": "유분", "score": 0~100 숫자만}
                  ],
                  "ingredients": [
                    {"name": "성분명"},
                    {"name": "성분명"},
                    {"name": "성분명"}
                  ],
                  "routine": [
                    {"name": "단계명", "product": "추천 제품 유형", "desc": "이유 1문장"},
                    {"name": "단계명", "product": "추천 제품 유형", "desc": "이유 1문장"},
                    {"name": "단계명", "product": "추천 제품 유형", "desc": "이유 1문장"},
                  ],
                  "disclaimer": "이 결과는 참고용이며 의학적 진단이 아닙니다."
                 }
                 이때 ingredients는 반드시 다음 목록에서만 3개를 선택하세요: %s
                 routine은 반드시 3단계로 구성하고, 실제 스킨케어 도포 순서대로 제시하세요.
                 각 단계의 product에는 위에서 선택한 ingredients 중 해당 단계에 어울리는 성분이 포함된 제품 유형을 추천하세요.
                 이때 앞에서 선택된 ingredients의 나열 순서(a, b, c)와 실제 routine의 단계별 순서는 전혀 일치할 필요가 없습니다.
                 오직 스킨케어의 올바른 도포 순서와 성분의 제형 궁합을 최우선으로 고려하여 유연하게 루틴을 구성하세요.
                """.formatted(surveyText, ingredientNames);

        SkinDiagnosisResult result;
        try {
            result = builder.build()
                    .prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userSpec -> userSpec
                            .text(prompt)
                            .media(mimeType, new ByteArrayResource(imageBytes))
                    )
                    .call()
                    .entity(SkinDiagnosisResult.class);
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            // 호출 자체 실패(네트워크/인증/타임아웃 등)
            throw new ApplicationException(DiagnosisErrorCase.LLM_CALL_FAILED, e);
        }

        // 구조화 매핑은 됐지만 핵심 필드가 비어 계약을 만족하지 못하는 경우
        if (result == null
                || result.totalScore() == null
                || result.metrics() == null
                || result.metrics().isEmpty()) {
            throw new ApplicationException(DiagnosisErrorCase.LLM_RESPONSE_INVALID);
        }
        return result;
    }

    private static final String SYSTEM_PROMPT = """
            당신은 사용자의 피부 사진과 설문을 바탕으로 피부 상태를 분석해 주는 AI 도우미입니다.
            - 결과는 참고용이며 의학적 진단이 아닙니다. 질병 진단·치료를 단정하지 마세요.
            - 반드시 요청된 JSON 스키마만 출력하고, 그 외 설명 문장은 포함하지 마세요.
            - 모든 점수는 0~100 사이 정수로만 답하세요.
            """;
}