package com.nuro.server.user.service;

import com.nuro.server.global.exception.ApplicationException;
import com.nuro.server.user.dto.request.UserRegisterRequest;
import com.nuro.server.user.dto.response.UserResponse;
import com.nuro.server.user.entity.User;
import com.nuro.server.user.exception.UserErrorCase;
import com.nuro.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        User user = User.create(request.nickname(), request.age());
        userRepository.save(user);
        return UserResponse.from(user);
    }

    public UserResponse getUser(Long userId) {
        return UserResponse.from(userRepository.findById(userId).
                orElseThrow(()-> new ApplicationException(UserErrorCase.USER_NOT_FOUND)));
    }
}