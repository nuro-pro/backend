package com.nuro.server.diagnosis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI피부 진단 요청 객체(설문 답변)")
public record DiagnosisRequest(
        @Schema(description = "세안 후 아무것도 바르지 않앗을 때 피부 상태", example = "금방 건조하고 당겨요")
        String skinCondition,

        @Schema(description = "가장 신경쓰이는 피부 고민", example = "트러블이 나요")
        String skinConcern,

        @Schema(description = "새로운 화장품 사용 시 피부 반응", example = "쉽게 붉어져요")
        String skinSensitivity
) { }
