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
    public void addQuestion(SurveyQuestionRequest questionRequest){
        SurveyQuestion surveyQuestion = SurveyQuestion.create(questionRequest.comment());
        surveyQuestionRepository.save(surveyQuestion);
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
    public void addAnswer(SurveyAnswerRequest request){
        SurveyQuestion question = surveyQuestionRepository.findById(request.questionId())
                .orElseThrow(()->new ApplicationException(SurveyErrorCase.SURVEY_QUESTION_NOT_FOUND));
        // 관리자 직접 추가 응답은 특정 사용자에 귀속되지 않음(userId null)
        SurveyAnswer surveyAnswer = SurveyAnswer.create(question, request.comment(), null);
        surveyAnswerRepository.save(surveyAnswer);
    }

    // 진단 플로우 설문 응답을 사용자(userId)에 귀속해 저장
    @Transactional
    public void saveDiagnosisAnswers(Long userId, Map<DiagnosisSurveyQuestion, String> answers) {
        answers.forEach((question, comment) -> {
            if (comment == null || comment.isBlank()) {
                return;
            }
            SurveyQuestion surveyQuestion = surveyQuestionRepository.findByCode(question.code())
                    .orElseThrow(() -> new ApplicationException(SurveyErrorCase.SURVEY_QUESTION_NOT_FOUND));
            surveyAnswerRepository.save(SurveyAnswer.create(surveyQuestion, comment, userId));
        });
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