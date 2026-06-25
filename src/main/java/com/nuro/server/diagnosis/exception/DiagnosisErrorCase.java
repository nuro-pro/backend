package com.nuro.server.diagnosis.exception;

import com.nuro.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiagnosisErrorCase implements ErrorCase {

    IMAGE_TOO_LARGE(400, 4001, "이미지 용량이 허용 범위를 초과했습니다."),
    UNSUPPORTED_IMAGE_TYPE(400, 4002, "지원하지 않는 이미지 형식입니다."),
    IMAGE_PROCESSING_FAILED(500, 4003, "이미지 처리에 실패했습니다."),
    DIAGNOSIS_NOT_FOUND(404, 4004, "진단 결과를 찾을 수 없습니다."),
    LLM_CALL_FAILED(502, 4005, "AI 호출에 실패했습니다."),
    LLM_RESPONSE_INVALID(502, 4006, "AI 응답을 해석하지 못했습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}