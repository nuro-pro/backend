package com.nuro.server.diagnosis.controller;

import com.nuro.server.diagnosis.dto.response.DiagnosisResponse;
import com.nuro.server.diagnosis.exception.DiagnosisErrorCase;
import com.nuro.server.diagnosis.service.DiagnosisService;
import com.nuro.server.global.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DiagnosisController.class)
@DisplayName("DiagnosisController 슬라이스 테스트")
class DiagnosisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnosisService diagnosisService;

    private DiagnosisResponse sampleResponse() {
        return new DiagnosisResponse(
                1L, "건성", 24, 53, "수분 관리가 필요합니다.", "건조함이 두드러집니다.",
                List.of(new DiagnosisResponse.MetricDto("수분", 62)),
                List.of(new DiagnosisResponse.IngredientDto(
                        "히알루론산", "Hyaluronic acid", 1, "낮음", "적당함",
                        "깊은 보습 성분", List.of("깊은 보습"), "세안 > 토너", "촉촉할 때 발라요")),
                List.of(new DiagnosisResponse.RoutineDto("토너", "토너 패드", "각질 정리")),
                "이 결과는 참고용이며 의학적 진단이 아닙니다."
        );
    }

    @Test
    @DisplayName("성공 - 이미지+설문 업로드 시 진단 결과를 CommonResponse로 반환한다")
    void diagnose_success() throws Exception {
        given(diagnosisService.diagnose(anyLong(), any(), any())).willReturn(sampleResponse());

        MockMultipartFile imagePart = new MockMultipartFile(
                "image", "face.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});
        MockMultipartFile surveyPart = new MockMultipartFile(
                "survey", "", MediaType.APPLICATION_JSON_VALUE,
                "{\"skinCondition\":\"건조\",\"skinConcern\":\"트러블\",\"skinSensitivity\":\"붉어짐\"}".getBytes());

        mockMvc.perform(multipart("/api/v1/diagnoses")
                        .file(imagePart)
                        .file(surveyPart)
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.totalScore").value(53))
                .andExpect(jsonPath("$.data.skinType").value("건성"))
                .andExpect(jsonPath("$.data.metrics[0].name").value("수분"))
                .andExpect(jsonPath("$.data.ingredients[0].korName").value("히알루론산"));
    }

    @Test
    @DisplayName("성공 - ID로 진단 결과를 조회한다")
    void getDiagnosis_success() throws Exception {
        given(diagnosisService.getDiagnosis(1L)).willReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/diagnoses/{diagnosisId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.totalScore").value(53));
    }

    @Test
    @DisplayName("실패 - 없는 ID 조회 시 404와 errorCode를 반환한다")
    void getDiagnosis_notFound() throws Exception {
        given(diagnosisService.getDiagnosis(99L))
                .willThrow(new ApplicationException(DiagnosisErrorCase.DIAGNOSIS_NOT_FOUND));

        mockMvc.perform(get("/api/v1/diagnoses/{diagnosisId}", 99L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(DiagnosisErrorCase.DIAGNOSIS_NOT_FOUND.getErrorCode()));
    }
}