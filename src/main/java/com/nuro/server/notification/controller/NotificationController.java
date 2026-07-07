package com.nuro.server.notification.controller;

import com.nuro.server.global.response.CommonResponse;
import com.nuro.server.notification.dto.request.SmsSendRequest;
import com.nuro.server.notification.dto.response.SmsSendResponse;
import com.nuro.server.notification.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(name = "알림", description = "진단 결과 문자(SMS) 전송 (SMS 연동 미구성 — 스켈레톤)")
public class NotificationController {

    private final SmsService smsService;

    @Operation(summary = "진단 결과 문자 전송", description = "진단 결과 요약/링크를 지정 번호로 전송합니다.")
    @PostMapping("/{shareId}/sms")
    public CommonResponse<SmsSendResponse> sendDiagnosisResult(
            @PathVariable String shareId,
            @Valid @RequestBody SmsSendRequest request) {
        smsService.sendResult(shareId, request.phoneNumber());
        return CommonResponse.success(SmsSendResponse.ok());
    }
}