package com.nuro.server.diagnosis.enums;

/**
 * 진단 처리 상태
 * 비전 LLM 호출은 느리고 실패 가능하므로 상태를 명시적으로 관리
 */
public enum DiagnosisStatus {
    ANALYZING,   // 이미지 분석중 (LLM 호출 진행)
    COMPLETED,   // 진단 완료
    FAILED       // LLM 호출/매핑 실패
}