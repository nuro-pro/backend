package com.nuro.server.ingredient.controller;

import com.nuro.server.global.response.CommonResponse;
import com.nuro.server.ingredient.dto.response.IngredientResponse;
import com.nuro.server.ingredient.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ingredients")
@Tag(name = "성분 추천", description = "진단 결과 기반 '나와 잘 맞는 성분' (확장 예정)")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(summary = "진단 기반 추천 성분 조회", description = "진단 결과에 맞는 추천 성분 목록을 반환합니다.")
    @GetMapping("/recommendations")
    public CommonResponse<List<IngredientResponse>> recommend(@RequestParam Long diagnosisId) {
        return CommonResponse.success(ingredientService.recommendForDiagnosis(diagnosisId));
    }
}