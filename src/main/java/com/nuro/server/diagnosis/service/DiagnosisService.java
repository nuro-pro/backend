package com.nuro.server.diagnosis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuro.server.diagnosis.client.SkinDiagnosisClient;
import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
import com.nuro.server.diagnosis.dto.request.DiagnosisRequest;
import com.nuro.server.diagnosis.dto.response.DiagnosisResponse;
import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.diagnosis.entity.DiagnosisSurveyAnswer;
import com.nuro.server.diagnosis.enums.DiagnosisStatus;
import com.nuro.server.diagnosis.exception.DiagnosisErrorCase;
import com.nuro.server.diagnosis.repository.DiagnosisRepository;
import com.nuro.server.diagnosis.repository.DiagnosisSurveyAnswerRepository;
import com.nuro.server.diagnosis.storage.ImageStorage;
import com.nuro.server.diagnosis.util.ImageResizer;
import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.ingredient.entity.Ingredient;
import com.nuro.server.ingredient.repository.IngredientRepository;
import com.nuro.server.survey.entity.SurveyAnswer;
import com.nuro.server.survey.enums.DiagnosisSurveyQuestion;
import com.nuro.server.survey.exception.SurveyErrorCase;
import com.nuro.server.survey.repository.SurveyAnswerRepository;
import com.nuro.server.survey.service.SurveyService;
import com.nuro.server.user.dto.response.UserResponse;
import com.nuro.server.user.entity.User;
import com.nuro.server.user.exception.UserErrorCase;
import com.nuro.server.user.repository.UserRepository;
import com.nuro.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisService {

    private static final Set<String> SUPPORTED_MIME_TYPES =
            Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisSurveyAnswerRepository diagnosisSurveyAnswerRepository;
    private final SkinDiagnosisClient skinDiagnosisClient;
    private final ImageStorage imageStorage;
    private final ImageResizer imageResizer;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final SurveyService surveyService;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * 사진 업로드 → 검증 → 리사이즈 → 비전 LLM 호출 → 저장된 결과 영속화
     */
    @Transactional
    public DiagnosisResponse diagnose(Long userId, MultipartFile image, DiagnosisRequest request) {
        validateImage(image);

        // 사용자 검증 + 또래 평균 비교에 쓸 실제 나이 확보(LLM 호출 전에 빠르게 실패)
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ApplicationException(UserErrorCase.USER_NOT_FOUND));

        MimeType mimeType = resolveMimeType(image);
        byte[] resized = resizeForLlm(image, mimeType);
        List<SurveyAnswer> answers = request.answers().stream()
                .map(item -> surveyAnswerRepository.findById(item.answerId())
                        .orElseThrow(()-> new ApplicationException(SurveyErrorCase.SURVEY_ANSWER_NOT_FOUND)))
                .toList();

        //설문 결과
        String surveyText = answers.stream().map(
                answer -> "- "+answer.getQuestion().getContent()
                +": "+answer.getComment()
        ).collect(Collectors.joining());

        //ingredient 목록
        String ingredientNames = ingredientRepository.findAll().stream()
                .map(Ingredient::getKorName)
                .collect(Collectors.joining(", "));

        // 외부 LLM 호출
        SkinDiagnosisResult aiResult = skinDiagnosisClient.diagnose(
                resized,
                mimeType,
                surveyText,
                ingredientNames
        );
        // 성공한 결과만 이미지 저장 + 영속화
        String imageUrl = imageStorage.store(resized, mimeType);

        Diagnosis diagnosis = Diagnosis.start(userId, imageUrl);
        diagnosis.complete(aiResult, toJson(aiResult));
        diagnosisRepository.save(diagnosis);

        List<DiagnosisSurveyAnswer> diagnosisAnswers =
                answers.stream()
                        .map(answer ->
                                DiagnosisSurveyAnswer.create(
                                        user,
                                        diagnosis,
                                        answer
                                ))
                        .toList();

        diagnosisSurveyAnswerRepository.saveAll(diagnosisAnswers);

        Map<String, Integer> peerScores = getAverageScore(user.getAge());

        int peerTotalScore =
                (peerScores.get("수분")
                        + peerScores.get("주름")
                        + peerScores.get("색소")
                        + peerScores.get("모공")
                        + peerScores.get("민감")
                        + peerScores.get("유분")) / 6;
        String presignedUrl = imageStorage.generateGetUrl(diagnosis.getImageUrl());
        return DiagnosisResponse.from(diagnosis, aiResult, peerScores, peerTotalScore, user, presignedUrl);
    }

    // 진단 ID로 저장된 결과를 조회
    public DiagnosisResponse getDiagnosis(String sharedId) {
        Diagnosis diagnosis = diagnosisRepository.findByShareId(sharedId)
                .orElseThrow(() -> new ApplicationException(DiagnosisErrorCase.DIAGNOSIS_NOT_FOUND));

        SkinDiagnosisResult result = fromJson(diagnosis.getRawResult());
        User user = userRepository.findById(diagnosis.getUserId())
                .orElseThrow(()-> new ApplicationException(UserErrorCase.USER_NOT_FOUND));

        Map<String, Integer> peerScores = getAverageScore(user.getAge());

        int peerTotalScore =
                (peerScores.get("수분")
                        + peerScores.get("주름")
                        + peerScores.get("색소")
                        + peerScores.get("모공")
                        + peerScores.get("민감")
                        + peerScores.get("유분")) / 6;

        String presignedUrl = imageStorage.generateGetUrl(diagnosis.getImageUrl());
        return DiagnosisResponse.from(diagnosis, result, peerScores, peerTotalScore, user, presignedUrl);
    }

    // 평균값 계산
    private Map<String, Integer> getAverageScore(Integer userAge){
        Integer startAge = userAge-5;
        Integer endAge = userAge+5;
        List<User> users= userRepository.findByAgeBetween(startAge, endAge);

        if (users.isEmpty()) {
            throw new ApplicationException(UserErrorCase.USER_NOT_FOUND);
        }

        List<Long> userIdList = users.stream().map(User::getId).toList();
        List<Diagnosis> diagnoses = diagnosisRepository.findByUserIdInAndStatus(userIdList, DiagnosisStatus.COMPLETED);

        int avgMoisture = (int) Math.round(diagnoses.stream()
                .mapToInt(Diagnosis::getMoistureScore)
                .average()
                .orElse(0.0));

        int avgWrinkle = (int) Math.round(diagnoses.stream()
                .mapToInt(Diagnosis::getWrinkleScore)
                .average()
                .orElse(0.0));

        int avgPigment = (int) Math.round(diagnoses.stream()
                .mapToInt(Diagnosis::getPigmentScore)
                .average()
                .orElse(0.0));

        int avgPore = (int) Math.round(diagnoses.stream()
                .mapToInt(Diagnosis::getPoreScore)
                .average()
                .orElse(0.0));

        int avgSensitive = (int) Math.round(diagnoses.stream()
                .mapToInt(Diagnosis::getSensitiveScore)
                .average()
                .orElse(0.0));

        int avgOil = (int) Math.round(diagnoses.stream()
                .mapToInt(Diagnosis::getOilScore)
                .average()
                .orElse(0.0));

        return new HashMap<>() {
            {
                put("수분", avgMoisture);
                put("주름", avgWrinkle);
                put("색소", avgPigment);
                put("모공", avgPore);
                put("민감", avgSensitive);
                put("유분", avgOil);
            }
        };
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