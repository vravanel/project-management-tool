package com.exercice.project_management_tool.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
                @Schema(description = "Adresse email du compte", example = "vincent@test.com") @NotBlank String email,
                @Schema(description = "Mot de passe", example = "motdepasse123") @NotBlank String password) {
}
