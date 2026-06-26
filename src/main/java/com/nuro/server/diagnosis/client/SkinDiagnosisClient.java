package com.nuro.server.diagnosis.client;

import com.nuro.server.diagnosis.exception.DiagnosisErrorCase;
import com.nuro.server.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

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
                                        String skinCondition,
                                        String skinConcern,
                                        String skinSensitivity) {
        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        if (builder == null) {
            // AI 비활성(none) 또는 모델 미구성
            throw new ApplicationException(DiagnosisErrorCase.LLM_CALL_FAILED);
        }
        String prompt = """
                당신은 전문 피부과 AI 분석가입니다.
                아래 사용자 정보와 피부 사진을 바탕으로 피부를 분석해주세요.
                [사용자 설문 응답]
                - 세안 후 아무것도 바르지 않았을 때 피부 상태: %s
                - 주요 피부 고민: %s
                - 새로운 화장품 사용시 피부 반응: %s
                
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
                    {"name": "성분명", "badge": "효능 한 단어", "desc": "이 성분이 이 피부에 좋은 이유 1~2문장"},
                    {"name": "성분명", "badge": "효능 한 단어", "desc": "이 성분이 이 피부에 좋은 이유 1~2문장"},
                    {"name": "성분명", "badge": "효능 한 단어", "desc": "이 성분이 이 피부에 좋은 이유 1~2문장"}
                  ],
                  "routine": [
                    {"name": "클렌징", "product": "추천 제품 유형", "desc": "이유 1문장"},
                    {"name": "토너", "product": "추천 제품 유형", "desc": "이유 1문장"},
                    {"name": "세럼", "product": "추천 제품 유형", "desc": "이유 1문장"},
                    {"name": "수분크림", "product": "추천 제품 유형", "desc": "이유 1문장"},
                    {"name": "선크림", "product": "추천 제품 유형", "desc": "이유 1문장"}
                  ],
                  "disclaimer": "이 결과는 참고용이며 의학적 진단이 아닙니다."
                 }
                """.formatted(skinCondition, skinConcern, skinSensitivity);

        return builder.build()
                .prompt()
                .user(userSpec -> userSpec
                        .text(prompt)
                        .media(mimeType, new ByteArrayResource(imageBytes))
                )
                .call()
                .entity(SkinDiagnosisResult.class);

        //throw new UnsupportedOperationException("TODO: SkinDiagnosisClient.diagnose 구현 필요");
    }
}