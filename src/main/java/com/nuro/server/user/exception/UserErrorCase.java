package com.nuro.server.user.exception;

import com.nuro.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 도메인 에러
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCase implements ErrorCase {

    USER_NOT_FOUND(404, 4101, "사용자를 찾을 수 없습니다."),
    INVALID_NICKNAME(400, 4102, "닉네임이 유효하지 않습니다."),
    INVALID_AGE(400, 4103, "나이가 유효하지 않습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}