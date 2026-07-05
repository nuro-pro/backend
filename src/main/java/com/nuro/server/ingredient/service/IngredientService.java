package com.nuro.server.ingredient.service;

import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.ingredient.dto.request.IngredientRequest;
import com.nuro.server.ingredient.dto.response.IngredientResponse;
import com.nuro.server.ingredient.entity.Ingredient;
import com.nuro.server.ingredient.exception.IngredientErrorCase;
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

    //추가
    @Transactional
    public Long addIngredient(IngredientRequest request){
        Ingredient ingredient = Ingredient.create(
                request.korName(),
                request.engName(),
                request.ewgGrade(),
                request.riskLevel(),
                request.dataLevel(),
                request.desc(),
                request.effects(),
                request.howToUse(),
                request.tip()
            );
        ingredientRepository.save(ingredient);
        return ingredient.getId();
    }

    //전체 불러오기
    public List<IngredientResponse> getAllIngredients(){
        return ingredientRepository.findAllByDeletedAtIsNullOrderById()
                .stream()
                .map(IngredientResponse::from)
                .toList();
    }

    //삭제
    @Transactional
    public void deleteIngredient(Long ingredientId){
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(()-> new ApplicationException(IngredientErrorCase.INGREDIENT_NOT_FOUND));

        ingredient.softDelete();
    }

}