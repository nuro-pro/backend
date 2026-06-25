package com.nuro.server.diagnosis.controller;

import com.nuro.server.diagnosis.dto.response.DiagnosisResponse;
import com.nuro.server.diagnosis.service.DiagnosisService;
import com.nuro.server.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "피부 사진 진단",
            description = "얼굴 이미지를 업로드하면 구조화된 진단 결과를 반환합니다. 결과는 참고용이며 의학적 진단이 아닙니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<DiagnosisResponse> diagnose(
            @RequestParam Long userId,
            @RequestPart("image") MultipartFile image
    ) {
        return CommonResponse.success(diagnosisService.diagnose(userId, image));
    }

    @Operation(summary = "진단 결과 조회", description = "진단 ID로 결과(피부타입/지표/종합점수)를 조회합니다.")
    @GetMapping("/{diagnosisId}")
    public CommonResponse<DiagnosisResponse> getDiagnosis(@PathVariable Long diagnosisId) {
        return CommonResponse.success(diagnosisService.getDiagnosis(diagnosisId));
    }
}