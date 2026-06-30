package com.nuro.server.survey.dto.request;

/**
 * 설문 일괄 제출 요청
 */
public record SurveyAnswerRequest(
       Long questionId,
       String comment
) {
}