package com.nuro.server.survey.controller;

import com.nuro.server.global.response.CommonResponse;
import com.nuro.server.survey.dto.request.SurveyAnswerRequest;
import com.nuro.server.survey.dto.request.SurveyQuestionRequest;
import com.nuro.server.survey.dto.response.SurveyAnswerResponse;
import com.nuro.server.survey.dto.response.SurveyQuestionResponse;
import com.nuro.server.survey.dto.response.SurveyQuestionWithAnswerResponse;
import com.nuro.server.survey.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
@Tag(name = "설문", description = "피부 설문 문항 조회 및 응답 제출")
public class SurveyController {

    private final SurveyService surveyService;

    // ===== 일반 유저용 =====

    @Operation(summary = "문항-응답 전체 구조 조회 (일반 유저)")
    @GetMapping
    public CommonResponse<List<SurveyQuestionWithAnswerResponse>> getSurveyFull() {
        return CommonResponse.success(surveyService.getSurveyFull());
    }

    // ===== 관리자용 =====
    // ===== 설문 문항
    @Operation(summary = "설문 문항 추가 (관리자)")
    @PostMapping("/admin/questions/add")
    public CommonResponse<?> addQuestion(@RequestBody @Valid SurveyQuestionRequest request) {
        surveyService.addQuestion(request);
        return CommonResponse.success();
    }

    @Operation(summary = "설문 문항 삭제(비활성화) (관리자)")
    @DeleteMapping("/admin/questions/{questionId}")
    public CommonResponse<?> deleteQuestion(@PathVariable Long questionId) {
        surveyService.deleteQuestion(questionId);
        return CommonResponse.success();
    }

    @Operation(summary = "전체 설문 문항 조회")
    @GetMapping("/admin/questions")
    public CommonResponse<List<SurveyQuestionResponse>> getQuestions() {
        return CommonResponse.success(surveyService.getQuestions());
    }

    //===설문 응답

    @Operation(summary = "전체 설문 응답 조회 (관리자)")
    @GetMapping("/admin/answers")
    public CommonResponse<List<SurveyAnswerResponse>> getAllAnswers() {
        return CommonResponse.success(surveyService.getAllAnswers());
    }

    @Operation(summary = "설문 응답 삭제 (관리자)")
    @DeleteMapping("/admin/answers/{answerId}")
    public CommonResponse<?> deleteAnswer(@PathVariable Long answerId) {
        surveyService.deleteAnswer(answerId);
        return CommonResponse.success();
    }

    @Operation(summary = "설문 응답 추가")
    @PostMapping("/admin/answers/add")
    public CommonResponse<?> addAnswer(@RequestBody @Valid SurveyAnswerRequest request) {
        surveyService.addAnswer(request);
        return CommonResponse.success();
    }

}