package com.nuro.server.notification.service;

import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.diagnosis.repository.DiagnosisRepository;
import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.notification.exception.NotificationErrorCase;
import com.nuro.server.user.entity.User;
import com.nuro.server.user.repository.UserRepository;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private static final int NICK_MAX_BYTES = 14; // … 여유 포함, SMS 90byte 사수

    private final DefaultMessageService messageService;
    private final DiagnosisRepository diagnosisRepository;
    private final UserRepository userRepository;

    @Value("${solapi.sender}")
    private String sender;

    @Value("${app.result-base-url}")
    private String resultBaseUrl;

    public void sendResult(String shareId, String rawPhone) {
        String phone = PhoneNumberUtil.normalize(rawPhone);
        if (!PhoneNumberUtil.isValidKoreanMobile(phone)) {
            throw new ApplicationException(NotificationErrorCase.INVALID_PHONE_NUMBER);
        }

        Diagnosis diagnosis = diagnosisRepository.findByShareId(shareId)
                .orElseThrow(() -> new ApplicationException(NotificationErrorCase.NOTIFICATION_SEND_FAILED));

        User user = userRepository.findById(diagnosis.getUserId())
                .orElseThrow(() -> new ApplicationException(NotificationErrorCase.NOTIFICATION_SEND_FAILED));

        String nickname = PhoneNumberUtil.truncateByBytes(user.getNickname(), NICK_MAX_BYTES);
        String url = resultBaseUrl + "/" + diagnosis.getShareId();
        String text = "[nuro] " + nickname + "님의 피부분석결과: " + url;

        Message message = new Message();
        message.setFrom(sender);
        message.setTo(phone);
        message.setText(text); // 길이에 따라 SMS/LMS 자동 판별

        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            messageService.send(message);
        } catch (SolapiMessageNotReceivedException exception) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}