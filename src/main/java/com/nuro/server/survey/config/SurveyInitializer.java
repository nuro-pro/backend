package com.nuro.server.survey.config;

import com.nuro.server.survey.entity.SurveyAnswer;
import com.nuro.server.survey.entity.SurveyQuestion;
import com.nuro.server.survey.repository.SurveyAnswerRepository;
import com.nuro.server.survey.repository.SurveyQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyInitializer implements ApplicationRunner {
    private final SurveyQuestionRepository questionRepository;
    private final SurveyAnswerRepository answerRepository;

    @Override
    public void run(ApplicationArguments args){
        //이미 데이터 있으면 생성 x
        if(questionRepository.count() > 0){
            return ;
        }
        SurveyQuestion q1 = questionRepository.save(
                SurveyQuestion.create("세안 후 아무것도 바르지 않았을 때 피부가 어떤가요 ?")
        );

        SurveyQuestion q2 = questionRepository.save(
                SurveyQuestion.create("가장 신경쓰이는 피부 고민은 무엇인가요?")
        );

        SurveyQuestion q3 = questionRepository.save(
                SurveyQuestion.create("새로운 화장품을 사용하면 피부가 어떤가요?")
        );

        answerRepository.saveAll(List.of(
                SurveyAnswer.create(q1, "금방 건조하고 당겨요"),
                SurveyAnswer.create(q1, "조금 건조한 편이에요"),
                SurveyAnswer.create(q1, "적당히 괜찮아요"),
                SurveyAnswer.create(q1, "번들거려요"),

                SurveyAnswer.create(q2, "피부가 건조해요"),
                SurveyAnswer.create(q2, "트러블이 나요"),
                SurveyAnswer.create(q2, "피부톤이 칙칙해요"),
                SurveyAnswer.create(q2, "탄력이 떨어진 것 같아요"),

                SurveyAnswer.create(q3, "쉽게 붉어져요"),
                SurveyAnswer.create(q3, "따갑거나 가려워요"),
                SurveyAnswer.create(q3, "별다른 변화가 없어요"),
                SurveyAnswer.create(q3, "잘 모르겠어요")

        ));
    }
}
