package com.nuro.server.survey.config;

import com.nuro.server.survey.entity.SurveyQuestion;
import com.nuro.server.survey.enums.DiagnosisSurveyQuestion;
import com.nuro.server.survey.repository.SurveyQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyQuestionSeeder implements ApplicationRunner {

    private final SurveyQuestionRepository surveyQuestionRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (DiagnosisSurveyQuestion question : DiagnosisSurveyQuestion.values()) {
            if (surveyQuestionRepository.findByCode(question.code()).isEmpty()) {
                surveyQuestionRepository.save(
                        SurveyQuestion.create(question.code(), question.getContent()));
                log.info("진단 설문 문항 시드: {}", question.code());
            }
        }
    }
}