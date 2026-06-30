package com.nuro.server.survey.repository;

import com.nuro.server.survey.entity.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
    //active true인 모든 질문 조회
    List<SurveyQuestion> findAllByDeletedAtIsNullOrderById();
}