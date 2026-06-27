package com.nuro.server.diagnosis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuro.server.diagnosis.dto.request.DiagnosisRequest;
import com.nuro.server.diagnosis.dto.response.DiagnosisResponse;
import com.nuro.server.diagnosis.service.DiagnosisService;
import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.global.exception.GlobalErrorCase;
import com.nuro.server.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diagnoses")
@Tag(name = "피부 진단", description = "사진 업로드 → AI 진단 (서비스 핵심)")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "피부 사진 진단",
            description = "얼굴 이미지를 업로드하면 구조화된 진단 결과를 반환합니다. 결과는 참고용이며 의학적 진단이 아닙니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<DiagnosisResponse> diagnose(
            @RequestParam Long userId,
            @RequestPart("image") MultipartFile image,
            @RequestPart("survey")
            @Schema(description = "설문 응답 JSON 문자열", type = "string",
                    example = "{\"skinCondition\":\"금방 건조하고 당겨요\",\"skinConcern\":\"트러블이 나요\",\"skinSensitivity\":\"쉽게 붉어져요\"}")
            String surveyJson
    ) {
        DiagnosisRequest survey = parseSurvey(surveyJson);
        return CommonResponse.success(diagnosisService.diagnose(userId, image, survey));
    }

    @Operation(summary = "진단 결과 조회", description = "진단 ID로 결과(피부타입/지표/종합점수)를 조회합니다.")
    @GetMapping("/{diagnosisId}")
    public CommonResponse<DiagnosisResponse> getDiagnosis(@PathVariable Long diagnosisId) {
        return CommonResponse.success(diagnosisService.getDiagnosis(diagnosisId));
    }

    private DiagnosisRequest parseSurvey(String surveyJson) {
        if (surveyJson == null || surveyJson.isBlank()) {
            throw new ApplicationException(GlobalErrorCase.INVALID_INPUT);
        }
        try {
            return objectMapper.readValue(surveyJson, DiagnosisRequest.class);
        } catch (Exception e) {
            throw new ApplicationException(GlobalErrorCase.INVALID_INPUT, e);
        }
    }
}