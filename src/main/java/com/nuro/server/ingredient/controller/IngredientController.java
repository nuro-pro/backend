package com.nuro.server.ingredient.controller;

import com.nuro.server.global.response.CommonResponse;
import com.nuro.server.ingredient.dto.request.IngredientRequest;
import com.nuro.server.ingredient.dto.response.IngredientResponse;
import com.nuro.server.ingredient.service.IngredientService;
import com.nuro.server.survey.dto.response.SurveyAnswerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ingredients")
@Tag(name = "성분 관리")
public class IngredientController {

    private final IngredientService ingredientService;

    //===diagnosis에서 ingredients 목록 불러오기 ===
    @Operation(summary = "전체 성분 조회 (관리자)")
    @GetMapping("/admin")
    public CommonResponse<List<IngredientResponse>> getAllIngredients() {
        return CommonResponse.success(ingredientService.getAllIngredients());
    }

    //===관리자 페이지 ingredients 관리 ===
    @Operation(summary = "성분 추가", description = "ai가 추천할 수 있는 성분 목록을 추가합니다.")
    @PostMapping("/admin/add")
    public CommonResponse<?> addIngredients(@RequestBody @Valid IngredientRequest request){
        ingredientService.addIngredient(request);
        return CommonResponse.success();
    }

    @Operation(summary = "성분 삭제", description = "ai가 추천할 수 있는 성분 목록을 삭제합니다.")
    @DeleteMapping("/admin/{ingredientId}")
    public CommonResponse<?> deleteIngredients(@PathVariable Long ingredientId){
        ingredientService.deleteIngredient(ingredientId);
        return CommonResponse.success();
    }

}