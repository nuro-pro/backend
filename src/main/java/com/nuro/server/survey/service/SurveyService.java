package com.nuro.server.survey.service;

import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.survey.dto.request.SurveyAnswerRequest;
import com.nuro.server.survey.dto.request.SurveyQuestionRequest;
import com.nuro.server.survey.dto.response.SurveyAnswerResponse;
import com.nuro.server.survey.dto.response.SurveyQuestionResponse;
import com.nuro.server.survey.dto.response.SurveyQuestionWithAnswerResponse;
import com.nuro.server.survey.entity.SurveyAnswer;
import com.nuro.server.survey.entity.SurveyQuestion;
import com.nuro.server.survey.enums.DiagnosisSurveyQuestion;
import com.nuro.server.survey.exception.SurveyErrorCase;
import com.nuro.server.survey.repository.SurveyAnswerRepository;
import com.nuro.server.survey.repository.SurveyQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;

    //===설문 문항
    //질문 추가
    @Transactional
    public Long addQuestion(SurveyQuestionRequest questionRequest){
        SurveyQuestion surveyQuestion = SurveyQuestion.create(questionRequest.comment());
        surveyQuestionRepository.save(surveyQuestion);
        return surveyQuestion.getId();
    }

    //질문 삭제 (비활성화)
    @Transactional
    public void deleteQuestion(Long questionId){
        SurveyQuestion question = surveyQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ApplicationException(SurveyErrorCase.SURVEY_QUESTION_NOT_FOUND));
        question.softDelete();
    }

    //전체질문 get
    public List<SurveyQuestionResponse> getQuestions() {
        return surveyQuestionRepository.findAllByDeletedAtIsNullOrderById()
                .stream()
                .map(SurveyQuestionResponse::from)
                .toList();
    }

    // ===답변
    //답변 추가
    @Transactional
    public Long addAnswer(SurveyAnswerRequest request){
        SurveyQuestion question = surveyQuestionRepository.findById(request.questionId())
                .orElseThrow(()->new ApplicationException(SurveyErrorCase.SURVEY_QUESTION_NOT_FOUND));
        SurveyAnswer surveyAnswer = SurveyAnswer.create(question, request.comment());
        surveyAnswerRepository.save(surveyAnswer);
        return surveyAnswer.getId();
    }

    //답변 get
    public List<SurveyAnswerResponse> getAllAnswers() {
        return surveyAnswerRepository.findAllByDeletedAtIsNullOrderById()
                .stream()
                .map(SurveyAnswerResponse::from)
                .toList();
    }


    //특정 문항에 대한 답변 목록
    public List<SurveyAnswerResponse> getSurveyAnswers(Long questionId) {
        return surveyAnswerRepository
                .findAllByDeletedAtIsNullAndQuestionIdOrderById(questionId)
                .stream()
                .map(SurveyAnswerResponse::from)
                .toList();
    }

    //답변 삭제
    @Transactional
    public void deleteAnswer(Long answerId) {
        SurveyAnswer answer = surveyAnswerRepository.findById(answerId)
                .orElseThrow(() -> new ApplicationException(SurveyErrorCase.SURVEY_ANSWER_NOT_FOUND));

        answer.softDelete();
    }

    public List<SurveyQuestionWithAnswerResponse> getSurveyFull(){
        return surveyQuestionRepository.findAllByDeletedAtIsNullOrderById()
                .stream()
                .map(q -> new SurveyQuestionWithAnswerResponse(
                        q.getId(),
                        q.getContent(),
                        q.getAnswers().stream()
                                .filter(a -> a.getDeletedAt()==null)
                                .map(SurveyAnswerResponse::from)
                                .toList()
                ))
                .toList();
    }
}