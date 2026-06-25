package com.nuro.server.survey.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 설문 일괄 제출 요청
 */
public record SurveyAnswerSubmitRequest(

        @NotNull(message = "사용자 ID가 필요합니다.")
        Long userId,

        @NotEmpty(message = "설문 응답이 비어있습니다.")
        @Valid
        List<AnswerItem> answers
) {
    public record AnswerItem(

            @NotNull(message = "문항 ID가 필요합니다.")
            Long questionId,

            @NotNull(message = "선택지를 선택해주세요.")
            Integer choiceNo
    ) {
    }
}