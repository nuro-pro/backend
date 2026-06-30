package com.nuro.server.diagnosis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuro.server.diagnosis.client.SkinDiagnosisClient;
import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
import com.nuro.server.diagnosis.dto.request.DiagnosisRequest;
import com.nuro.server.diagnosis.dto.response.DiagnosisResponse;
import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.diagnosis.exception.DiagnosisErrorCase;
import com.nuro.server.diagnosis.repository.DiagnosisRepository;
import com.nuro.server.diagnosis.storage.ImageStorage;
import com.nuro.server.diagnosis.util.ImageResizer;
import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.survey.enums.DiagnosisSurveyQuestion;
import com.nuro.server.survey.service.SurveyService;
import com.nuro.server.user.dto.response.UserResponse;
import com.nuro.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisService {

    private static final Set<String> SUPPORTED_MIME_TYPES =
            Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final DiagnosisRepository diagnosisRepository;
    private final SkinDiagnosisClient skinDiagnosisClient;
    private final ImageStorage imageStorage;
    private final ImageResizer imageResizer;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final SurveyService surveyService;

    /**
     * 사진 업로드 → 검증 → 리사이즈 → 비전 LLM 호출 → 저장된 결과 영속화
     */
    @Transactional
    public DiagnosisResponse diagnose(Long userId, MultipartFile image, DiagnosisRequest request) {
        validateImage(image);

        // 사용자 검증 + 또래 평균 비교에 쓸 실제 나이 확보(LLM 호출 전에 빠르게 실패)
        UserResponse user = userService.getUser(userId);

        MimeType mimeType = resolveMimeType(image);
        byte[] resized = resizeForLlm(image, mimeType);

        // 외부 LLM 호출
        SkinDiagnosisResult aiResult = skinDiagnosisClient.diagnose(
                resized,
                mimeType,
                request.skinCondition(),
                request.skinConcern(),
                request.skinSensitivity()
        );

        // 성공한 결과만 이미지 저장 + 영속화
        String imageUrl = imageStorage.store(resized, mimeType);

        Diagnosis diagnosis = Diagnosis.start(userId, imageUrl);
        diagnosis.complete(aiResult, toJson(aiResult));
        diagnosisRepository.save(diagnosis);

        // 사용자의 설문 응답을 userId에 귀속해 저장
        surveyService.saveDiagnosisAnswers(userId, toSurveyAnswers(request));

        return DiagnosisResponse.from(diagnosis, aiResult, user.age());
    }

    // 진단 ID로 저장된 결과를 조회
    public DiagnosisResponse getDiagnosis(Long diagnosisId) {
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId)
                .filter(d -> !d.isDeleted())
                .orElseThrow(() -> new ApplicationException(DiagnosisErrorCase.DIAGNOSIS_NOT_FOUND));

        SkinDiagnosisResult result = fromJson(diagnosis.getRawResult());
        UserResponse user = userService.getUser(diagnosis.getUserId());
        return DiagnosisResponse.from(diagnosis, result, user.age());
    }

    // 진단 설문을 고정 문항 코드에 매핑
    private Map<DiagnosisSurveyQuestion, String> toSurveyAnswers(DiagnosisRequest request) {
        Map<DiagnosisSurveyQuestion, String> answers = new LinkedHashMap<>();
        answers.put(DiagnosisSurveyQuestion.SKIN_CONDITION, request.skinCondition());
        answers.put(DiagnosisSurveyQuestion.SKIN_CONCERN, request.skinConcern());
        answers.put(DiagnosisSurveyQuestion.SKIN_SENSITIVITY, request.skinSensitivity());
        return answers;
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ApplicationException(DiagnosisErrorCase.IMAGE_PROCESSING_FAILED);
        }
        String contentType = image.getContentType();
        if (contentType == null
                || !SUPPORTED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new ApplicationException(DiagnosisErrorCase.UNSUPPORTED_IMAGE_TYPE);
        }
    }

    private MimeType resolveMimeType(MultipartFile image) {
        String contentType = image.getContentType();
        return contentType != null ? MimeType.valueOf(contentType) : MimeTypeUtils.IMAGE_JPEG;
    }

    private byte[] resizeForLlm(MultipartFile image, MimeType mimeType) {
        try {
            return imageResizer.resize(image.getBytes(), mimeType);
        } catch (IOException e) {
            throw new ApplicationException(DiagnosisErrorCase.IMAGE_PROCESSING_FAILED, e);
        }
    }

    private String toJson(SkinDiagnosisResult result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            log.warn("진단 결과 직렬화 실패: {}", e.getMessage());
            return null;
        }
    }

    private SkinDiagnosisResult fromJson(String rawResult) {
        if (rawResult == null || rawResult.isBlank()) {
            throw new ApplicationException(DiagnosisErrorCase.LLM_RESPONSE_INVALID);
        }
        try {
            return objectMapper.readValue(rawResult, SkinDiagnosisResult.class);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(DiagnosisErrorCase.LLM_RESPONSE_INVALID, e);
        }
    }
}