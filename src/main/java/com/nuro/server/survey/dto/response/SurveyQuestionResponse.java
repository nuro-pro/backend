package com.nuro.server.survey.dto.response;

import com.nuro.server.survey.entity.SurveyQuestion;

import java.util.List;

/**
 * 설문 문항 응답 (문항 + 4지선다 선택지)
 */
public record SurveyQuestionResponse(
        Long questionId,
        String content
) {
    public static SurveyQuestionResponse from(SurveyQuestion question) {
        return new SurveyQuestionResponse(
                question.getId(),
                question.getContent()
        );
    }
}