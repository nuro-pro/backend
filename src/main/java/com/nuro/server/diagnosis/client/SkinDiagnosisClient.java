package com.nuro.server.diagnosis.client;

import com.nuro.server.diagnosis.exception.DiagnosisErrorCase;
import com.nuro.server.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
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
    public SkinDiagnosisResult diagnose(byte[] imageBytes, MimeType mimeType) {
        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        if (builder == null) {
            // AI 비활성(none) 또는 모델 미구성
            throw new ApplicationException(DiagnosisErrorCase.LLM_CALL_FAILED);
        }

        throw new UnsupportedOperationException("TODO: SkinDiagnosisClient.diagnose 구현 필요");
    }
}