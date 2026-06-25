package com.nuro.server.diagnosis.entity;

import com.nuro.server.diagnosis.enums.DiagnosisStatus;
import com.nuro.server.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 피부 진단 1건
 * 흐름: 사진 업로드 → 리사이즈 → 비전 LLM 호출(고정 JSON 스키마) → 구조화 결과 영속화
 */
@Entity
@Getter
@Table(name = "diagnosis")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diagnosis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // 저장된(리사이즈된) 진단 이미지 위치 (S3 key 또는 로컬 경로)
    @Column(nullable = false, length = 500)
    private String imageUrl;

    // 피부 타입 (예: 건성/지성/복합성 등 LLM 분류 결과)
    @Column(length = 50)
    private String skinType;

    // 종합 점수
    @Column
    private Integer totalScore;

    // AI 코멘트 요약 (사용자 노출용, 면책 문구 포함)
    @Column(columnDefinition = "TEXT")
    private String summary;

    // LLM 원문 응답(JSON) 보관
    @Column(columnDefinition = "TEXT")
    private String rawResult;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiagnosisStatus status;

    @OneToMany(mappedBy = "diagnosis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiagnosisMetric> metrics = new ArrayList<>();

    private Diagnosis(Long userId, String imageUrl, DiagnosisStatus status) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // 분석 시작 시점 생성
    public static Diagnosis start(Long userId, String imageUrl) {
        return new Diagnosis(userId, imageUrl, DiagnosisStatus.ANALYZING);
    }

    // LLM 결과 반영 후 완료 처리 -> TODO: 파라미터/지표 매핑 구체화.
    public void complete(String skinType, Integer totalScore, String summary, String rawResult) {
        // TODO: 결과 필드 채우고 status를 COMPLETED로 전환, metrics 연관 추가
        throw new UnsupportedOperationException("TODO: Diagnosis.complete 구현 필요");
    }

    public void fail() {
        // TODO: status FAILED 전환
        throw new UnsupportedOperationException("TODO: Diagnosis.fail 구현 필요");
    }
}