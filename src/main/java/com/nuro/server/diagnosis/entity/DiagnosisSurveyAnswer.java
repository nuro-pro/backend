package com.nuro.server.diagnosis.entity;

import com.nuro.server.global.entity.BaseEntity;
import com.nuro.server.survey.entity.SurveyAnswer;
import com.nuro.server.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisSurveyAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="diagnosis_id",nullable = false)
    private Diagnosis diagnosis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_answer_id", nullable = false)
    private SurveyAnswer answer;

    private DiagnosisSurveyAnswer(User user, Diagnosis diagnosis, SurveyAnswer answer){
        this.user = user;
        this.diagnosis = diagnosis;
        this.answer=answer;
    }

    public static DiagnosisSurveyAnswer create(User user, Diagnosis diagnosis, SurveyAnswer answer){
        return new DiagnosisSurveyAnswer(user, diagnosis, answer);
    }
}
