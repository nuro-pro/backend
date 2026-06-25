package com.nuro.server.routine.service;

import com.nuro.server.routine.dto.response.RoutineResponse;
import com.nuro.server.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineService {

    private final RoutineRepository routineRepository;

    /**
     * 진단 결과에 맞는 추천 루틴
     * TODO: 진단(피부타입/지표) 기반 단계 구성 — 규칙 기반 또는 LLM 추천
     */
    public RoutineResponse recommendForDiagnosis(Long diagnosisId) {
        throw new UnsupportedOperationException("TODO: RoutineService.recommendForDiagnosis 구현 필요");
    }
}