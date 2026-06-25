package com.nuro.server.survey.entity;

import com.nuro.server.global.entity.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 설문 문항
 * 문항/선택지는 고정 콘텐츠라 마스터 데이터로 관리
 */
@Entity
@Getter
@Table(name = "survey_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 노출 순서 (설문 1, 2)
    @Column(nullable = false)
    private Integer questionNo;

    @Column(nullable = false, length = 200)
    private String content;

    // 4지선다 선택지 텍스트
    @ElementCollection
    @CollectionTable(name = "survey_question_choice", joinColumns = @JoinColumn(name = "survey_question_id"))
    @Column(name = "choice_text", nullable = false, length = 100)
    private List<String> choices;

    private SurveyQuestion(Integer questionNo, String content, List<String> choices) {
        this.questionNo = questionNo;
        this.content = content;
        this.choices = choices;
    }

    public static SurveyQuestion create(Integer questionNo, String content, List<String> choices) {
        return new SurveyQuestion(questionNo, content, choices);
    }
}