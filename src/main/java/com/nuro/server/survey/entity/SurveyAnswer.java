package com.nuro.server.survey.entity;

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
 * 사용자가 제출한 설문 응답 1건
 */
@Entity
@Getter
@Table(name = "survey_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_question_id", nullable = false)
    private SurveyQuestion question;

    // 선택한 선택지 번호
    @Column(nullable = false)
    private Integer choiceNo;

    private SurveyAnswer(Long userId, SurveyQuestion question, Integer choiceNo) {
        this.userId = userId;
        this.question = question;
        this.choiceNo = choiceNo;
    }

    public static SurveyAnswer create(Long userId, SurveyQuestion question, Integer choiceNo) {
        return new SurveyAnswer(userId, question, choiceNo);
    }
}