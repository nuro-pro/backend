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
                  "totalDesc": "종합 점수 근거를 짚은 한 줄 요약",
                  "summary": "피부 상태 전반에 대한 3~4문장 구체 분석",
                  "metrics": [
                    {"name": "수분", "score": 0~100 숫자만, "comment": "이 지표에 대한 1문장 분석"},
                    {"name": "주름", "score": 0~100 숫자만, "comment": "이 지표에 대한 1문장 분석"},
                    {"name": "색소", "score": 0~100 숫자만, "comment": "이 지표에 대한 1문장 분석"},
                    {"name": "모공", "score": 0~100 숫자만, "comment": "이 지표에 대한 1문장 분석"},
                    {"name": "민감", "score": 0~100 숫자만, "comment": "이 지표에 대한 1문장 분석"},
                    {"name": "유분", "score": 0~100 숫자만, "comment": "이 지표에 대한 1문장 분석"}
                  ],
                  "ingredients": [
                    {"name": "성분명"},
                    {"name": "성분명"},
                    {"name": "성분명"}
                  ],
                  "routine": [
                    {"name": "단계명(제품 카테고리)", "product": "실제 시판 제품명(브랜드 포함)", "desc": "이 단계가 필요한 이유 1문장"},
                    {"name": "단계명(제품 카테고리)", "product": "실제 시판 제품명(브랜드 포함)", "desc": "이 단계가 필요한 이유 1문장"},
                    {"name": "단계명(제품 카테고리)", "product": "실제 시판 제품명(브랜드 포함)", "desc": "이 단계가 필요한 이유 1문장"}
                  ],
                  "disclaimer": "이 결과는 참고용이며 의학적 진단이 아닙니다."
                 }
                 summary와 totalDesc는 두루뭉술한 일반론 대신, 아래를 반드시 반영해 구체적으로 작성하세요.
                 - 6개 지표 중 점수가 낮은(관리가 필요한) 지표와 높은(양호한) 지표를 실제로 언급할 것
                 - 사진에서 관찰된 부위별 특징(예: T존/볼/눈가 등)과 설문 응답에서 드러난 고민을 근거로 연결할 것
                 - 마지막에 지금 가장 우선해야 할 관리 방향을 1가지로 제시할 것
                 - 해요체로, 진단을 단정하지 말고 관찰·경향 위주로("~보여요", "~필요해요") 서술할 것
                 metrics의 각 comment는 해당 지표의 점수와 사진·설문에서 관찰된 실제 상태를 근거로
                 지표마다 다르게, 1문장 해요체("~해요", "~보여요")로 작성하세요. 정형화된 문구를 반복하지 마세요.
                 이때 ingredients는 반드시 다음 목록에서만 3개를 선택하세요: %s
                 routine은 반드시 3단계로 구성하고, 실제 스킨케어 도포 순서대로 제시하세요.
                 - name에는 해당 단계의 제품 카테고리(예: 토너, 토너 패드, 세럼, 앰플, 크림 등)를 넣으세요.
                 - product에는 위에서 선택한 ingredients 중 해당 단계 성분이 포함된 "실제 시중에서 판매되는" 스킨케어 제품명을
                   브랜드와 함께 구체적으로 추천하세요(예: "메디힐 히알루론산 속보습 더마 패드"). 단, 실제로 존재하지 않는
                   제품을 지어내지 말고, 널리 알려진 실제 제품 위주로만 제시하세요. 확실하지 않으면 대표적인 실제 제품으로 대체하세요.
                 - desc는 단계마다 서로 다르게, 그 단계와 성분이 이 사용자의 피부 상태에 왜 맞는지를 1문장 해요체로 쓰세요(문구 반복 금지).
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
            - 사용자에게 노출되는 모든 설명 문구(totalDesc, summary, metrics의 comment, routine의 desc)는
              친근한 해요체("~해요", "~보여요", "~필요해요")로 작성하세요.
            """;
}