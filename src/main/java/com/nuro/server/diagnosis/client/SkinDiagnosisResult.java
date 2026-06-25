package com.nuro.server.diagnosis.client;

import java.util.List;

/**
 * 비전 LLM의 고정 JSON 스키마 구조화 출력 대상 타입
 * 진단 핵심(피부타입/종합점수/지표/요약/면책)만 둔다. 성분·루틴 추천은 별도 도메인(ingredient/routine)에서
 * TODO: 지표 종류(수분/유분/주름/색소/모공/트러블 등 6개) 확정 후 시스템 프롬프트와 일치시켜야함.
 */
public record SkinDiagnosisResult(
        String skinType,
        Integer totalScore,
        String summary,
        List<MetricResult> metrics,
        String disclaimer   // "참고용이며 의학적 진단이 아님"
) {
    public record MetricResult(
            String name,
            Integer score
    ) {
    }
}