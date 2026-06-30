package com.nuro.server.user.dto.response;

import com.nuro.server.user.entity.User;

public record UserResponse(
        Long id,
        String username,
        String nickname,
        Integer age
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getNickname(), user.getAge());
    }
}