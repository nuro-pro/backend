package com.nuro.server.routine.exception;

import com.nuro.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 루틴 도메인 에러
 */
@Getter
@RequiredArgsConstructor
public enum RoutineErrorCase implements ErrorCase {

    ROUTINE_NOT_FOUND(404, 4401, "추천 루틴을 찾을 수 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}