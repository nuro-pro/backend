package com.nuro.server.diagnosis.dto.response;

import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.ingredient.enums.Ingredients;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    public record IngredientDto(
            String korName,
            String engName,
            int ewgGrade,
            String riskLevel,
            String dataLevel,
            String desc,
            List<String> effects,
            String howToUse,
            String tip
    ) {}

    public record RoutineDto(String name, String product, String desc) {}

    public static DiagnosisResponse from(Diagnosis diagnosis, SkinDiagnosisResult result) {
        List<MetricDto> metricDtos = result.metrics().stream()
                .map(m -> new MetricDto(m.name(), m.score()))
                .toList();

        List<IngredientDto> ingredientDtos = result.ingredients().stream()
                .map(i -> {
                    Ingredients ingredient = Ingredients.findByKorName(i.name());
                    if (ingredient == null) return null;
                    return new IngredientDto(
                            ingredient.getKorName(),
                            ingredient.getEngName(),
                            ingredient.getEwgGrade(),
                            ingredient.getRiskLevel(),
                            ingredient.getDataLevel(),
                            ingredient.getDesc(),
                            ingredient.getEffects(),
                            ingredient.getHowToUse(),
                            ingredient.getTip()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        List<RoutineDto> routineDtos = result.routine().stream()
                .map(r -> new RoutineDto(r.name(), r.product(), r.desc()))
                .toList();

        return new DiagnosisResponse(
                diagnosis.getId(),
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