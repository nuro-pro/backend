package com.nuro.server.user.controller;

import com.nuro.server.global.response.CommonResponse;
import com.nuro.server.user.dto.request.UserRegisterRequest;
import com.nuro.server.user.dto.response.UserResponse;
import com.nuro.server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "사용자", description = "온보딩 단계 익명 사용자(닉네임/나이) 관리")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 등록", description = "온보딩 단계에서 닉네임/나이를 입력받아 익명 사용자를, 회원가입을 통해 사용자를 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return CommonResponse.success(userService.register(request));
    }

    @Operation(summary = "사용자 조회", description = "사용자 ID로 닉네임/나이를 조회합니다.")
    @GetMapping("/{userId}")
    public CommonResponse<UserResponse> getUser(@PathVariable Long userId) {
        return CommonResponse.success(userService.getUser(userId));
    }
}