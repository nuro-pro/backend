package com.nuro.server.survey.repository;

import com.nuro.server.survey.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
    List<SurveyAnswer> findAllByDeletedAtIsNullAndQuestionIdOrderById(Long questionId);
    List<SurveyAnswer> findAllByDeletedAtIsNullOrderById();

    // 특정 사용자의 설문 응답
    List<SurveyAnswer> findAllByDeletedAtIsNullAndUserIdOrderById(Long userId);
}