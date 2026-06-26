package com.nuro.server.diagnosis.dto.response;

import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
import com.nuro.server.diagnosis.entity.Diagnosis;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 진단 결과 API 응답, LLM 출력 타입(SkinDiagnosisResult)과 분리
 * 외부 노출 응답에는 면책 문구를 포함
 */
public record DiagnosisResponse(
        Long id,
        String skinType,
        Integer skinAge,
        Integer totalScore,
        String totalDesc,
        String summary,
        List<MetricDto> metrics,
        List<IngredientDto> ingredients,
        List<RoutineDto> routine,
        String disclaimer
) {
    public record MetricDto(String name, Integer score) {}

    public record IngredientDto(String name, String badge, String desc) {}

    public record RoutineDto(String name, String product, String desc) {}

    public static DiagnosisResponse from(SkinDiagnosisResult result) {
        List<MetricDto> metricDtos = result.metrics().stream()
                .map(m -> new MetricDto(m.name(), m.score()))
                .toList();

        List<IngredientDto> ingredientDtos = result.ingredients().stream()
                .map(i -> new IngredientDto(i.name(), i.badge(), i.desc()))
                .toList();

        List<RoutineDto> routineDtos = result.routine().stream()
                .map(r -> new RoutineDto(r.name(), r.product(), r.desc()))
                .toList();

        return new DiagnosisResponse(
                null,                    // id: DB 저장 후 채워짐
                result.skinType(),
                result.skinAge(),
                result.totalScore(),
                result.totalDesc(),
                result.summary(),
                metricDtos,
                ingredientDtos,
                routineDtos,
                result.disclaimer()
        );
    }
}