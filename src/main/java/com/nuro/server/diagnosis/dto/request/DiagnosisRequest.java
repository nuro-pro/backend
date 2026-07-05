package com.nuro.server.diagnosis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "AI피부 진단 요청 객체(설문 답변)")
public record DiagnosisRequest(
        @Schema(description = "설문 응답 목록")
        List<SurveyAnswerItem> answers
){
        @Schema(description = "설문 응답 항목")
        public record SurveyAnswerItem(
                @Schema(description = "선택지 ID", example = "1")
                Long answerId,

                @Schema(description = "문항 ID", example = "1")
                Long questionId
        ){}
}
