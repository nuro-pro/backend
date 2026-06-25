package com.nuro.server.routine.controller;

import com.nuro.server.global.response.CommonResponse;
import com.nuro.server.routine.dto.response.RoutineResponse;
import com.nuro.server.routine.service.RoutineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routines")
@Tag(name = "루틴 추천", description = "진단 결과 기반 '추천 스킨케어 루틴' (확장 예정)")
public class RoutineController {

    private final RoutineService routineService;

    @Operation(summary = "진단 기반 추천 루틴 조회", description = "진단 결과에 맞는 추천 스킨케어 루틴(단계별)을 반환합니다.")
    @GetMapping("/recommendations")
    public CommonResponse<RoutineResponse> recommend(@RequestParam Long diagnosisId) {
        return CommonResponse.success(routineService.recommendForDiagnosis(diagnosisId));
    }
}