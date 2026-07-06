package com.nuro.server.ingredient.dto.response;

import com.nuro.server.ingredient.entity.Ingredient;

import java.util.List;

public record IngredientResponse(
        //관리자 페이지 ingredient 목록에 렌더링할 항목 가려내야함
        Long ingredientId,
        String korName,
        String engName,
        Integer ewgGrade,
        String riskLevel,
        String dataLevel,
        String desc,
        List<String> effects,
        String howToUse,
        String tip
) {
    public static IngredientResponse from(Ingredient ingredient){
        return new IngredientResponse(
                ingredient.getId(),
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
    }
}
