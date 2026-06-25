package com.nuro.server.survey.repository;

import com.nuro.server.survey.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

    List<SurveyAnswer> findAllByUserId(Long userId);
}