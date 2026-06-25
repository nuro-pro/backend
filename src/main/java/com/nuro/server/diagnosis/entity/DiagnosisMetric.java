package com.nuro.server.diagnosis.entity;

import com.nuro.server.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 진단 항목별 지표 1건 ("6개 지표 밸런스", "항목별 분석: 수분 62 ")
 * 지표 종류(수분/유분/주름/색소 등)는 LLM 고정 스키마와 일치해야 한다
 */
@Entity
@Getter
@Table(name = "diagnosis_metric")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisMetric extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;

    // 지표명 (예: 수분, 유분, 주름, 색소, 모공, 트러블)
    @Column(nullable = false, length = 30)
    private String name;

    // 점수 (0~100)
    @Column(nullable = false)
    private Integer score;

    private DiagnosisMetric(Diagnosis diagnosis, String name, Integer score) {
        this.diagnosis = diagnosis;
        this.name = name;
        this.score = score;
    }

    public static DiagnosisMetric create(Diagnosis diagnosis, String name, Integer score) {
        return new DiagnosisMetric(diagnosis, name, score);
    }
}