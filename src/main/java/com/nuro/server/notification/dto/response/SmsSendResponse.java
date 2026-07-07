package com.nuro.server.notification.dto.response;

public record SmsSendResponse(
        boolean success,
        String message
) {
    public static SmsSendResponse ok() {
        return new SmsSendResponse(true, "문자를 전송했어요.");
    }
}