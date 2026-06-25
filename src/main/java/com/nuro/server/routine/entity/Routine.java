package com.nuro.server.routine.entity;

import com.nuro.server.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * 추천 스킨케어 루틴
 */
@Entity
@Getter
@Table(name = "routine")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Routine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 귀속 진단(다른 도메인)
    @Column(nullable = false)
    private Long diagnosisId;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineStep> steps = new ArrayList<>();

    private Routine(Long diagnosisId) {
        this.diagnosisId = diagnosisId;
    }

    public static Routine create(Long diagnosisId) {
        return new Routine(diagnosisId);
    }
}