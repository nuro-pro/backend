package com.nuro.server.notification.service;

import com.nuro.server.notification.client.NotificationSender;
import com.nuro.server.notification.dto.request.DiagnosisResultNotifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationSender notificationSender;

    /**
     * 진단 결과(요약/링크)를 문자로 전송
     * TODO: 진단 결과 조회 → 메시지 구성(요약 + 결과 링크) 외부 전송 실패는 NOTIFICATION_SEND_FAILED(502)로 변환
     */
    public void sendDiagnosisResult(DiagnosisResultNotifyRequest request) {
        throw new UnsupportedOperationException("TODO: NotificationService.sendDiagnosisResult 구현 필요");
    }
}