package com.nuro.server.ingredient.exception;

import com.nuro.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 성분 도메인 에러
 */
@Getter
@RequiredArgsConstructor
public enum IngredientErrorCase implements ErrorCase {

    INGREDIENT_NOT_FOUND(404, 4301, "성분 정보를 찾을 수 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}