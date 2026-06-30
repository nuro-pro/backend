package com.nuro.server.diagnosis.dto.response;

import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.diagnosis.enums.AgeBand;
import com.nuro.server.ingredient.enums.Ingredients;

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
        Integer peerTotalScore,
        String totalDesc,
        String summary,
        List<MetricDto> metrics,
        List<IngredientDto> ingredients,
        List<RoutineDto> routine,
        String disclaimer
) {
    public record MetricDto(String name, Integer score, Integer peerScore) {}

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
        // 이미지에서 추정된 나이대(skinAge)에 해당하는 또래 평균을 백엔드에서 결정
        AgeBand peerBand = AgeBand.of(result.skinAge());

        List<MetricDto> metricDtos = nullSafe(result.metrics()).stream()
                .filter(Objects::nonNull)
                .map(m -> new MetricDto(m.name(), m.score(), peerBand.peerScoreOf(m.name())))
                .toList();

        // 추천 성분은 반드시 Ingredients 카드(18종) 안에서만
        // LLM이 목록 밖 이름을 주면 매칭 실패 → 제외
        List<IngredientDto> ingredientDtos = nullSafe(result.ingredients()).stream()
                .filter(Objects::nonNull)
                .map(i -> Ingredients.findByName(i.name()))
                .filter(Objects::nonNull)
                .distinct()
                .map(ingredient -> new IngredientDto(
                        ingredient.getKorName(),
                        ingredient.getEngName(),
                        ingredient.getEwgGrade(),
                        ingredient.getRiskLevel(),
                        ingredient.getDataLevel(),
                        ingredient.getDesc(),
                        ingredient.getEffects(),
                        ingredient.getHowToUse(),
                        ingredient.getTip()
                ))
                .toList();

        List<RoutineDto> routineDtos = nullSafe(result.routine()).stream()
                .filter(Objects::nonNull)
                .map(r -> new RoutineDto(r.name(), r.product(), r.desc()))
                .toList();

        return new DiagnosisResponse(
                diagnosis.getId(),
                result.skinType(),
                result.skinAge(),
                result.totalScore(),
                peerBand.getPeerTotalScore(),
                result.totalDesc(),
                result.summary(),
                metricDtos,
                ingredientDtos,
                routineDtos,
                result.disclaimer()
        );
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list != null ? list : List.of();
    }
}