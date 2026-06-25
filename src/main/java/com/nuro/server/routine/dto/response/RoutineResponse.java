package com.nuro.server.routine.dto.response;

import com.nuro.server.routine.entity.Routine;

import java.util.List;

/**
 * 추천 루틴 응답
 */
public record RoutineResponse(
        Long id,
        Long diagnosisId,
        List<RoutineStepDto> steps
) {
    public record RoutineStepDto(
            Integer stepNo,
            String title,
            String description
    ) {
    }

    public static RoutineResponse from(Routine routine) {
        throw new UnsupportedOperationException("TODO: RoutineResponse.from 구현 필요");
    }
}