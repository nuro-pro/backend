package com.nuro.server.survey.entity;

import com.nuro.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    // 안정적 식별용 코드
    @Column(length = 50, unique = true)
    private String code;

    //질문 내용
    @Column(nullable = false, length = 200)
    private String content;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<SurveyAnswer> answers = new ArrayList<>();

    private SurveyQuestion(String code, String content) {
        this.code = code;
        this.content = content;
    }

    public static SurveyQuestion create(String content) {
        return new SurveyQuestion(null, content);
    }

    // 코드가 부여된 고정 문항(진단 설문 등) 생성용
    public static SurveyQuestion create(String code, String content) {
        return new SurveyQuestion(code, content);
    }
}