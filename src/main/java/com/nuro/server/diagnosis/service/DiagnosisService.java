package com.nuro.server.diagnosis.service;

import com.nuro.server.diagnosis.client.SkinDiagnosisClient;
import com.nuro.server.diagnosis.dto.response.DiagnosisResponse;
import com.nuro.server.diagnosis.repository.DiagnosisRepository;
import com.nuro.server.diagnosis.storage.ImageStorage;
import com.nuro.server.diagnosis.util.ImageResizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public DiagnosisResponse diagnose(Long userId, MultipartFile image) {
        throw new UnsupportedOperationException("TODO: DiagnosisService.diagnose 구현 필요");
    }

    public DiagnosisResponse getDiagnosis(Long diagnosisId) {
        throw new UnsupportedOperationException("TODO: DiagnosisService.getDiagnosis 구현 필요");
    }
}