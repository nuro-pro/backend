package com.nuro.server.survey.dto.response;

import com.nuro.server.survey.entity.SurveyAnswer;
import com.nuro.server.survey.entity.SurveyQuestion;

import java.util.List;

public record SurveyAnswerResponse(
        Long answerId,
        String comment
) {
    public static SurveyAnswerResponse from(SurveyAnswer answer) {
        return new SurveyAnswerResponse(
                answer.getId(),
                answer.getComment()
        );
    }
}
