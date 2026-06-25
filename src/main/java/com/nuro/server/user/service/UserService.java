package com.nuro.server.user.service;

import com.nuro.server.user.dto.request.UserRegisterRequest;
import com.nuro.server.user.dto.response.UserResponse;
import com.nuro.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        throw new UnsupportedOperationException("TODO: UserService.register 구현 필요");
    }

    public UserResponse getUser(Long userId) {
        throw new UnsupportedOperationException("TODO: UserService.getUser 구현 필요");
    }
}