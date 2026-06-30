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

@Entity
@Getter
@Table(name = "survey_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //질문 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_question_id", nullable = false)
    private SurveyQuestion question;

    //답변
    @Column(nullable = false)
    private String comment;

    // 응답한 사용자
    @Column(name = "user_id")
    private Long userId;

    private SurveyAnswer(
            SurveyQuestion question,
            String comment,
            Long userId
    ) {
        this.question = question;
        this.comment = comment;
        this.userId = userId;
    }

    public static SurveyAnswer create(
            SurveyQuestion question,
            String comment,
            Long userId
    ) {
        return new SurveyAnswer(question, comment, userId);
    }
}