package com.nuro.server.user.dto.response;

import com.nuro.server.user.entity.User;

public record UserResponse(
        Long id,
        String nickname,
        Integer age
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getNickname(), user.getAge());
    }
}