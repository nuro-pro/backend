package com.nuro.server.notification.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpNotificationSender implements NotificationSender {

    @Override
    public void send(String phoneNumber, String message) {
        // TODO: 실제 SMS 전송으로 교체. 현재는 전송하지 않음
        log.info("[NoOpNotificationSender] SMS 전송 생략 (미구현). length={}", message == null ? 0 : message.length());
    }
}