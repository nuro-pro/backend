package com.nuro.server.diagnosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuro.server.diagnosis.client.SkinDiagnosisClient;
import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
import com.nuro.server.diagnosis.dto.request.DiagnosisRequest;
import com.nuro.server.diagnosis.dto.response.DiagnosisResponse;
import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.diagnosis.exception.DiagnosisErrorCase;
import com.nuro.server.diagnosis.repository.DiagnosisRepository;
import com.nuro.server.diagnosis.storage.ImageStorage;
import com.nuro.server.diagnosis.util.ImageResizer;
import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.survey.service.SurveyService;
import com.nuro.server.user.dto.response.UserResponse;
import com.nuro.server.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiagnosisService 단위 테스트")
class DiagnosisServiceTest {

    @InjectMocks
    private DiagnosisService diagnosisService;

    @Mock
    private DiagnosisRepository diagnosisRepository;
    @Mock
    private SkinDiagnosisClient skinDiagnosisClient;
    @Mock
    private ImageStorage imageStorage;
    @Mock
    private ImageResizer imageResizer;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserService userService;
    @Mock
    private SurveyService surveyService;
    @Mock
    private MultipartFile image;

    // 실제 나이 24세 → 또래 비교는 20대(AgeBand.TWENTIES) 기준
    private final UserResponse user = new UserResponse(1L, "user01", "닉네임", 24);

    private final DiagnosisRequest survey =
            new DiagnosisRequest("금방 건조하고 당겨요", "트러블이 나요", "쉽게 붉어져요");

    private SkinDiagnosisResult sampleResult() {
        return new SkinDiagnosisResult(
                "건성", 24, 53, "전반적으로 수분 관리가 필요합니다.", "건조함이 두드러집니다.",
                List.of(
                        new SkinDiagnosisResult.MetricResult("수분", 62),
                        new SkinDiagnosisResult.MetricResult("주름", 32),
                        new SkinDiagnosisResult.MetricResult("색소", 94),
                        new SkinDiagnosisResult.MetricResult("모공", 62),
                        new SkinDiagnosisResult.MetricResult("민감", 62),
                        new SkinDiagnosisResult.MetricResult("유분", 62)
                ),
                List.of(
                        new SkinDiagnosisResult.IngredientResult("히알루론산"),
                        new SkinDiagnosisResult.IngredientResult("LHA"),
                        new SkinDiagnosisResult.IngredientResult("BHA")
                ),
                List.of(new SkinDiagnosisResult.RoutineResult("토너", "토너 패드", "각질 정리")),
                "이 결과는 참고용이며 의학적 진단이 아닙니다."
        );
    }

    @Nested
    @DisplayName("diagnose")
    class Diagnose {

        @Test
        @DisplayName("성공 - 이미지 검증·리사이즈 후 LLM 결과를 매핑해 반환한다")
        void success() throws Exception {
            byte[] original = new byte[]{1, 2, 3};
            byte[] resized = new byte[]{4, 5};
            given(image.isEmpty()).willReturn(false);
            given(image.getContentType()).willReturn("image/jpeg");
            given(image.getBytes()).willReturn(original);
            given(userService.getUser(1L)).willReturn(user);
            given(imageResizer.resize(any(byte[].class), any(MimeType.class))).willReturn(resized);
            given(skinDiagnosisClient.diagnose(any(), any(), anyString(), anyString(), anyString()))
                    .willReturn(sampleResult());
            given(imageStorage.store(any(), any())).willReturn("/uploads/diagnosis/x.jpg");
            given(objectMapper.writeValueAsString(any())).willReturn("{\"json\":true}");
            given(diagnosisRepository.save(any(Diagnosis.class))).willAnswer(inv -> inv.getArgument(0));

            DiagnosisResponse response = diagnosisService.diagnose(1L, image, survey);

            assertThat(response.skinType()).isEqualTo("건성");
            assertThat(response.totalScore()).isEqualTo(53);
            // skinAge=24 → 20대 구간(AgeBand.TWENTIES) 또래 평균
            assertThat(response.peerTotalScore()).isEqualTo(60);
            assertThat(response.metrics()).hasSize(6);
            assertThat(response.metrics().get(0).name()).isEqualTo("수분");
            assertThat(response.metrics().get(0).peerScore()).isEqualTo(60);
            assertThat(response.ingredients()).hasSize(3);
            assertThat(response.ingredients().get(0).korName()).isEqualTo("히알루론산");
            assertThat(response.ingredients().get(0).engName()).isNotBlank();
            assertThat(response.routine()).hasSize(1);
            then(skinDiagnosisClient).should().diagnose(any(), any(), anyString(), anyString(), anyString());
            // 설문 응답이 userId로 저장되는지 확인
            then(surveyService).should().saveDiagnosisAnswers(eq(1L), anyMap());
        }

        @Test
        @DisplayName("실패 - 지원하지 않는 형식 → UNSUPPORTED_IMAGE_TYPE, LLM 호출 안 함")
        void unsupportedType() {
            given(image.isEmpty()).willReturn(false);
            given(image.getContentType()).willReturn("application/pdf");

            assertThatThrownBy(() -> diagnosisService.diagnose(1L, image, survey))
                    .isInstanceOf(ApplicationException.class)
                    .extracting("errorCase")
                    .isEqualTo(DiagnosisErrorCase.UNSUPPORTED_IMAGE_TYPE);

            then(skinDiagnosisClient).should(never()).diagnose(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("실패 - 빈 이미지 → IMAGE_PROCESSING_FAILED")
        void emptyImage() {
            given(image.isEmpty()).willReturn(true);

            assertThatThrownBy(() -> diagnosisService.diagnose(1L, image, survey))
                    .isInstanceOf(ApplicationException.class)
                    .extracting("errorCase")
                    .isEqualTo(DiagnosisErrorCase.IMAGE_PROCESSING_FAILED);
        }
    }

    @Nested
    @DisplayName("getDiagnosis")
    class GetDiagnosis {

        @Test
        @DisplayName("성공 - 저장된 원문 JSON을 복원해 반환한다")
        void success() throws Exception {
            Diagnosis diagnosis = Diagnosis.start(1L, "/uploads/diagnosis/x.jpg");
            diagnosis.complete(sampleResult(), "{\"json\":true}");
            given(diagnosisRepository.findById(10L)).willReturn(Optional.of(diagnosis));
            given(objectMapper.readValue(anyString(), any(Class.class))).willReturn(sampleResult());
            given(userService.getUser(1L)).willReturn(user);

            DiagnosisResponse response = diagnosisService.getDiagnosis(10L);

            assertThat(response.totalScore()).isEqualTo(53);
            assertThat(response.ingredients()).hasSize(3);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID → DIAGNOSIS_NOT_FOUND")
        void notFound() {
            given(diagnosisRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> diagnosisService.getDiagnosis(99L))
                    .isInstanceOf(ApplicationException.class)
                    .extracting("errorCase")
                    .isEqualTo(DiagnosisErrorCase.DIAGNOSIS_NOT_FOUND);
        }
    }
}