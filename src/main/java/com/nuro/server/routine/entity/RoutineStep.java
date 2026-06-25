package com.nuro.server.routine.entity;

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
 * 루틴 단계 1건
 */
@Entity
@Getter
@Table(name = "routine_step")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoutineStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    // 단계 순서 (1~N)
    @Column(nullable = false)
    private Integer stepNo;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private RoutineStep(Routine routine, Integer stepNo, String title, String description) {
        this.routine = routine;
        this.stepNo = stepNo;
        this.title = title;
        this.description = description;
    }

    public static RoutineStep create(Routine routine, Integer stepNo, String title, String description) {
        return new RoutineStep(routine, stepNo, title, description);
    }
}