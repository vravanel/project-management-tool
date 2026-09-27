package com.exercice.project_management_tool.DTO;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank String email,
        @NotBlank String password) {
}
