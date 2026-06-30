package com.nuro.server.survey.exception;

import com.nuro.server.global.exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 설문 도메인 에러
 */
@Getter
@RequiredArgsConstructor
public enum SurveyErrorCase implements ErrorCase {

    SURVEY_QUESTION_NOT_FOUND(404, 4201, "설문 문항을 찾을 수 없습니다."),
    INVALID_SURVEY_ANSWER(400, 4202, "유효하지 않은 설문 응답입니다."),
    SURVEY_NOT_COMPLETED(400, 4203, "모든 설문에 응답하지 않았습니다."),
    SURVEY_ANSWER_NOT_FOUND(400, 4204, "설문 응답을 찾을 수 없습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String message;
}