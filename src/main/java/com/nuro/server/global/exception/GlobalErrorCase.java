package com.nuro.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 도메인 공통/전역 에러 케이스
// 도메인별 에러는 각 도메인 패키지에 별도 ErrorCase enum(implements ErrorCase)을 만들어 관리
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCase implements ErrorCase {

    INVALID_INPUT(400, 4000, "요청 값이 유효하지 않습니다."),
    RESOURCE_NOT_FOUND(404, 4040, "요청하신 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, 4050, "허용되지 않은 HTTP 메소드입니다."),
    INTERNAL_SERVER_ERROR(500, 5000, "서버 내부 오류가 발생했습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}