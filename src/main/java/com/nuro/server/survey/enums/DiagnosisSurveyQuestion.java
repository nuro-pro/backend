package com.nuro.server.survey.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 진단 플로우의 고정 설문 문항
@Getter
@RequiredArgsConstructor
public enum DiagnosisSurveyQuestion {

    SKIN_CONDITION("세안 후 아무것도 바르지 않았을 때 피부 상태"),
    SKIN_CONCERN("가장 신경쓰이는 피부 고민"),
    SKIN_SENSITIVITY("새로운 화장품 사용 시 피부 반응");

    private final String content;

    public String code() {
        return name();
    }
}