package com.exercice.project_management_tool.DTO;

import com.exercice.project_management_tool.Entity.User;

public record UserResponse(
        Long id,
        String username,
        String email) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail());
    }
}
