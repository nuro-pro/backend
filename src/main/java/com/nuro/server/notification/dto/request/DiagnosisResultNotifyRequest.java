package com.nuro.server.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 진단 결과 문자 전송 요청
 */
public record DiagnosisResultNotifyRequest(

        @NotNull(message = "진단 ID가 필요합니다.")
        Long diagnosisId,

        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(regexp = "^01[0-9]-?\\d{3,4}-?\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phoneNumber
) {
}