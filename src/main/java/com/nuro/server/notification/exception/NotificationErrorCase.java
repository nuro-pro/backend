package com.nuro.server.notification.exception;

import com.nuro.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 알림 도메인 에러
 */
@Getter
@RequiredArgsConstructor
public enum NotificationErrorCase implements ErrorCase {

    INVALID_PHONE_NUMBER(400, 4501, "유효하지 않은 전화번호입니다."),
    NOTIFICATION_SEND_FAILED(502, 4502, "알림 전송에 실패했습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}