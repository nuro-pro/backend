package com.nuro.server.survey.controller;

import com.nuro.server.global.response.CommonResponse;
import com.nuro.server.survey.dto.request.SurveyAnswerSubmitRequest;
import com.nuro.server.survey.dto.response.SurveyQuestionResponse;
import com.nuro.server.survey.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
@Tag(name = "설문", description = "피부 설문 문항 조회 및 응답 제출")
public class SurveyController {

    private final SurveyService surveyService;

    @Operation(summary = "설문 문항 목록 조회", description = "노출 순서대로 설문 문항과 선택지를 반환합니다.")
    @GetMapping("/questions")
    public CommonResponse<List<SurveyQuestionResponse>> getQuestions() {
        return CommonResponse.success(surveyService.getQuestions());
    }

    @Operation(summary = "설문 응답 제출", description = "사용자의 설문 응답을 일괄 저장합니다. (진단 입력 보조 데이터)")
    @PostMapping("/answers")
    public CommonResponse<?> submitAnswers(@Valid @RequestBody SurveyAnswerSubmitRequest request) {
        surveyService.submitAnswers(request);
        return CommonResponse.success();
    }
}