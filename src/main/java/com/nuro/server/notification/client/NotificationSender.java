package com.nuro.server.notification.client;

/**
 * 알림(SMS) 전송 추상화 -> 결과 진단 문자 전송용("결과 저장/공유/문자")
 */
public interface NotificationSender {

    /**
     * 지정 번호로 메시지를 전송한다
     *
     * @param phoneNumber 수신 번호
     * @param message     전송 본문
     */
    void send(String phoneNumber, String message);
}