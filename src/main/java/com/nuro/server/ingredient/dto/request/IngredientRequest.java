package com.nuro.server.ingredient.dto.request;

import java.util.List;

public record IngredientRequest(
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
}
