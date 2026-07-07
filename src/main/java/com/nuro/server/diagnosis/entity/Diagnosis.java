package com.nuro.server.diagnosis.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
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

    @Column(nullable = false, unique = true)
    private String shareId;

    @Column(nullable = false)
    private Long userId;

    // 저장된(리사이즈된) 진단 이미지 위치 (S3 key 또는 로컬 경로)
    @Column(nullable = false, length = 500)
    private String imageUrl;

    // ==== LLM 주요 결과
    // 피부 타입 (예: 건성/지성/복합성 등 LLM 분류 결과)
    @Column(length = 50)
    private String skinType;

    // 피부 나이
    @Column
    private Integer skinAge;

    // 종합 점수
    @Column
    private Integer totalScore;

    @Column(columnDefinition = "TEXT")
    private String totalDesc;

    // AI 코멘트 요약 (사용자 노출용, 면책 문구 포함)
    @Column(columnDefinition = "TEXT")
    private String summary;

    // LLM 원문 응답(JSON) 보관
    @Column(columnDefinition = "TEXT")
    private String rawResult;

    // ===== 고정 6개 지표 =====
    @Column
    private Integer moistureScore; // 수분

    @Column
    private Integer wrinkleScore;  // 주름

    @Column
    private Integer pigmentScore;  // 색소

    @Column
    private Integer poreScore;     // 모공

    @Column
    private Integer sensitiveScore; // 민감

    @Column
    private Integer oilScore;      // 유분

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiagnosisStatus status;

    private Diagnosis(Long userId, String imageUrl, DiagnosisStatus status) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.status = status;
        this.shareId = NanoIdUtils.randomNanoId();
    }

    // 분석 시작 시점 생성
    public static Diagnosis start(Long userId, String imageUrl) {
        return new Diagnosis(userId, imageUrl, DiagnosisStatus.ANALYZING);
    }

    // LLM 결과 반영 후 완료 처리
    public void complete(SkinDiagnosisResult result, String rawResult) {
        if(this.status != DiagnosisStatus.ANALYZING){
            throw  new IllegalStateException("ANALYZING 상태에서만 완료 처리 할 수 잇습니다.");
        }
        this.skinType = result.skinType();
        this.skinAge = result.skinAge();
        this.totalScore = result.totalScore();
        this.summary = result.summary();
        this.totalDesc = result.totalDesc();
        this.rawResult = rawResult;

        applyMetricScores(result.metrics());

        this.status = DiagnosisStatus.COMPLETED;
    }
    public void fail() {
        if (this.status == DiagnosisStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 진단은 실패 처리할 수 없습니다.");
        }
        this.status = DiagnosisStatus.FAILED;
    }

    private void applyMetricScores(List<SkinDiagnosisResult.MetricResult> metrics) {
        if (metrics == null) return;

        for (SkinDiagnosisResult.MetricResult metric : metrics) {
            if (metric == null || metric.name() == null) continue;

            Integer score = metric.score();
            switch (metric.name().trim()) {
                case "수분" -> this.moistureScore = score;
                case "주름" -> this.wrinkleScore = score;
                case "색소" -> this.pigmentScore = score;
                case "모공" -> this.poreScore = score;
                case "민감" -> this.sensitiveScore = score;
                case "유분" -> this.oilScore = score;
                default -> {
                    // 정의 외 지표는 무시 (원하면 예외로 바꿀 수 있음)
                }
            }
        }
    }
}