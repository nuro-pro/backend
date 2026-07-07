package com.nuro.server.diagnosis.dto.response;

import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.ingredient.enums.Ingredients;
import com.nuro.server.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 진단 결과 API 응답, LLM 출력 타입(SkinDiagnosisResult)과 분리
 * 외부 노출 응답에는 면책 문구를 포함
 */
public record DiagnosisResponse(
        Long id,
        String shareId,

        String userNickname,
        String userImage,
        Integer userAge,

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
    public record MetricDto(String name, Integer score, Integer peerScore, String comment) {}

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

    public static DiagnosisResponse from(Diagnosis diagnosis, SkinDiagnosisResult result, Map<String, Integer> peerScores, Integer peerTotalScore, User user, String presignedImageUrl) {

        List<MetricDto> metricDtos = nullSafe(result.metrics()).stream()
                .filter(Objects::nonNull)
                .map(m -> new MetricDto(m.name(), m.score(), getAverageScoreByName(m.name(), peerScores), nullSafeComment(m.comment())))
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
                diagnosis.getShareId(),

                user.getNickname(),
                presignedImageUrl,
                user.getAge(),

                result.skinType(),
                result.skinAge(),
                result.totalScore(),
                peerTotalScore,
                result.totalDesc(),
                result.summary(),
                metricDtos,
                ingredientDtos,
                routineDtos,
                result.disclaimer()
        );
    }

    private static Integer getAverageScoreByName(String name, Map<String, Integer> peerScore){
        return switch (name) {
            case "수분" -> peerScore.get("수분");
            case "주름" -> peerScore.get("주름");
            case "색소" -> peerScore.get("색소");
            case "모공" -> peerScore.get("모공");
            case "민감" -> peerScore.get("민감");
            case "유분" -> peerScore.get("유분");
            default -> throw new IllegalStateException("잘못된 형식입니다.");
        };
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list != null ? list : List.of();
    }

    // LLM이 comment를 누락/공백으로 주더라도 프론트가 그대로 뿌릴 수 있게 non-null 보장
    private static String nullSafeComment(String comment) {
        return (comment != null && !comment.isBlank()) ? comment : "이 지표는 특별한 이상 없이 무난한 상태예요.";
    }
}