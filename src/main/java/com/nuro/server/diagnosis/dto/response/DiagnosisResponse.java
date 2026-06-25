package com.nuro.server.diagnosis.dto.response;

import com.nuro.server.diagnosis.entity.Diagnosis;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 진단 결과 API 응답, LLM 출력 타입(SkinDiagnosisResult)과 분리
 * 외부 노출 응답에는 면책 문구를 포함
 */
public record DiagnosisResponse(
        Long id,
        Long userId,
        String imageUrl,
        String skinType,
        Integer totalScore,
        String summary,
        List<MetricDto> metrics,
        String disclaimer,
        LocalDateTime createdAt
) {
    public record MetricDto(
            String name,
            Integer score
    ) {
    }

    public static DiagnosisResponse from(Diagnosis diagnosis) {
        throw new UnsupportedOperationException("TODO: DiagnosisResponse.from 구현 필요");
    }
}