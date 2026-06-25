package com.nuro.server.ingredient.dto.response;

import com.nuro.server.ingredient.entity.Ingredient;

/**
 * 추천 성분 응답
 */
public record IngredientResponse(
        Long id,
        String name,
        String description,
        String matchReason   // 이 진단 결과에 추천되는 이유 (TODO: 추천 로직에서 채움)
) {
    public static IngredientResponse from(Ingredient ingredient, String matchReason) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getDescription(),
                matchReason
        );
    }
}