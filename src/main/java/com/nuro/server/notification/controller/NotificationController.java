package com.nuro.server.notification.controller;

import com.nuro.server.global.response.CommonResponse;
import com.nuro.server.notification.dto.request.DiagnosisResultNotifyRequest;
import com.nuro.server.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(name = "알림", description = "진단 결과 문자(SMS) 전송 (SMS 연동 미구성 — 스켈레톤)")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "진단 결과 문자 전송", description = "진단 결과 요약/링크를 지정 번호로 전송합니다.")
    @PostMapping("/diagnosis-result")
    public CommonResponse<?> sendDiagnosisResult(@Valid @RequestBody DiagnosisResultNotifyRequest request) {
        notificationService.sendDiagnosisResult(request);
        return CommonResponse.success();
    }
}