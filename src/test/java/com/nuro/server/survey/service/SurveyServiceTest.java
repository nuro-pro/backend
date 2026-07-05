package com.nuro.server.survey.service;

import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.survey.entity.SurveyAnswer;
import com.nuro.server.survey.entity.SurveyQuestion;
import com.nuro.server.survey.enums.DiagnosisSurveyQuestion;
import com.nuro.server.survey.exception.SurveyErrorCase;
import com.nuro.server.survey.repository.SurveyAnswerRepository;
import com.nuro.server.survey.repository.SurveyQuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("SurveyService 단위 테스트")
class SurveyServiceTest {

    @InjectMocks
    private SurveyService surveyService;

    @Mock
    private SurveyQuestionRepository surveyQuestionRepository;
    @Mock
    private SurveyAnswerRepository surveyAnswerRepository;

    private Map<DiagnosisSurveyQuestion, String> answers() {
        Map<DiagnosisSurveyQuestion, String> map = new LinkedHashMap<>();
        map.put(DiagnosisSurveyQuestion.SKIN_CONDITION, "금방 건조하고 당겨요");
        map.put(DiagnosisSurveyQuestion.SKIN_CONCERN, "트러블이 나요");
        map.put(DiagnosisSurveyQuestion.SKIN_SENSITIVITY, "쉽게 붉어져요");
        return map;
    }

    @Nested
    @DisplayName("saveDiagnosisAnswers")
    class SaveDiagnosisAnswers {

//        @Test
//        @DisplayName("성공 - 3개 응답이 userId로 귀속되어 저장된다")
//        void success() {
//            given(surveyQuestionRepository.findByCode(anyString()))
//                    .willAnswer(inv -> Optional.of(SurveyQuestion.create(inv.getArgument(0), "문항")));
//
//            surveyService.saveDiagnosisAnswers(7L, answers());
//
//            ArgumentCaptor<SurveyAnswer> captor = ArgumentCaptor.forClass(SurveyAnswer.class);
//            then(surveyAnswerRepository).should(org.mockito.Mockito.times(3)).save(captor.capture());
//            assertThat(captor.getAllValues())
//                    .allSatisfy(a -> assertThat(a.getUserId()).isEqualTo(7L));
//            assertThat(captor.getAllValues())
//                    .extracting(SurveyAnswer::getComment)
//                    .containsExactly("금방 건조하고 당겨요", "트러블이 나요", "쉽게 붉어져요");
//        }
//
//        @Test
//        @DisplayName("성공 - 빈 응답은 건너뛴다")
//        void skipBlank() {
//            given(surveyQuestionRepository.findByCode(anyString()))
//                    .willAnswer(inv -> Optional.of(SurveyQuestion.create(inv.getArgument(0), "문항")));
//
//            Map<DiagnosisSurveyQuestion, String> map = new LinkedHashMap<>();
//            map.put(DiagnosisSurveyQuestion.SKIN_CONDITION, "건조함");
//            map.put(DiagnosisSurveyQuestion.SKIN_CONCERN, "   ");
//            map.put(DiagnosisSurveyQuestion.SKIN_SENSITIVITY, null);
//
//            surveyService.saveDiagnosisAnswers(7L, map);
//
//            then(surveyAnswerRepository).should(org.mockito.Mockito.times(1)).save(org.mockito.ArgumentMatchers.any());
//        }
//
//        @Test
//        @DisplayName("실패 - 시드된 문항이 없으면 SURVEY_QUESTION_NOT_FOUND")
//        void questionMissing() {
//            given(surveyQuestionRepository.findByCode(anyString())).willReturn(Optional.empty());
//
//            assertThatThrownBy(() -> surveyService.saveDiagnosisAnswers(7L, answers()))
//                    .isInstanceOf(ApplicationException.class)
//                    .extracting("errorCase")
//                    .isEqualTo(SurveyErrorCase.SURVEY_QUESTION_NOT_FOUND);
//
//            then(surveyAnswerRepository).should(never()).save(org.mockito.ArgumentMatchers.any());
//        }
    }
}