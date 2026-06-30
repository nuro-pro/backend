package com.nuro.server.survey.dto.response;

import java.util.List;

public record SurveyQuestionWithAnswerResponse(
        Long questionId,
        String question, //질문
        List<SurveyAnswerResponse> options //거이에 대한 답변
) {
}
