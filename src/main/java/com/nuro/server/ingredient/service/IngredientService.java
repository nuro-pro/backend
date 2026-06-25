package com.nuro.server.ingredient.service;

import com.nuro.server.ingredient.dto.response.IngredientResponse;
import com.nuro.server.ingredient.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    /**
     * 진단 결과에 맞는 추천 성분 목록
     * TODO: 진단(피부타입/지표) 조회 후 매칭 규칙 또는 LLM 추천으로 성분 선별
     */
    public List<IngredientResponse> recommendForDiagnosis(Long diagnosisId) {
        throw new UnsupportedOperationException("TODO: IngredientService.recommendForDiagnosis 구현 필요");
    }
}