package com.nuro.server.diagnosis.service;

import com.nuro.server.diagnosis.client.SkinDiagnosisClient;
import com.nuro.server.diagnosis.client.SkinDiagnosisResult;
import com.nuro.server.diagnosis.dto.request.DiagnosisRequest;
import com.nuro.server.diagnosis.dto.response.DiagnosisResponse;
import com.nuro.server.diagnosis.entity.Diagnosis;
import com.nuro.server.diagnosis.repository.DiagnosisRepository;
import com.nuro.server.diagnosis.storage.ImageStorage;
import com.nuro.server.diagnosis.util.ImageResizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final SkinDiagnosisClient skinDiagnosisClient;
    private final ImageStorage imageStorage;
    private final ImageResizer imageResizer;

    /**
     * 사진 업로드 → 검증 → 리사이즈 → 저장 → 비전 LLM 호출 → 결과 영속화
     */
    @Transactional
    public DiagnosisResponse diagnose(Long userId, MultipartFile image, DiagnosisRequest diagnosisRequest) {
        try{
            byte[] imageBytes = image.getBytes();
            MimeType mimeType = image.getContentType() != null
                    ? MimeType.valueOf(image.getContentType())
                    : MimeTypeUtils.IMAGE_JPEG;

            SkinDiagnosisResult aiResult = skinDiagnosisClient.diagnose(
                    imageBytes,
                    mimeType,
                    diagnosisRequest.skinCondition(),
                    diagnosisRequest.skinConcern(),
                    diagnosisRequest.skinSensitivity()
            );

            return DiagnosisResponse.from(aiResult);

        }catch (IOException e){
            throw new RuntimeException("이미지 파일을 읽는데 실패했습니다.", e);
        }
    }

    public DiagnosisResponse getDiagnosis(Long diagnosisId) {
        throw new UnsupportedOperationException("TODO: DiagnosisService.getDiagnosis 구현 필요");
    }
}