package com.nuro.server.survey.dto.request;

import com.nuro.server.survey.entity.SurveyQuestion;

//질문 추가
public record SurveyQuestionRequest(
        String comment
) {
}
