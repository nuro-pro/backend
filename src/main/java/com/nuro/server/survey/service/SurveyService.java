package com.nuro.server.survey.service;

import com.nuro.server.survey.dto.request.SurveyAnswerSubmitRequest;
import com.nuro.server.survey.dto.response.SurveyQuestionResponse;
import com.nuro.server.survey.repository.SurveyAnswerRepository;
import com.nuro.server.survey.repository.SurveyQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;

    public List<SurveyQuestionResponse> getQuestions() {
        throw new UnsupportedOperationException("TODO: SurveyService.getQuestions 구현 필요");
    }

    @Transactional
    public void submitAnswers(SurveyAnswerSubmitRequest request) {
        throw new UnsupportedOperationException("TODO: SurveyService.submitAnswers 구현 필요");
    }
}