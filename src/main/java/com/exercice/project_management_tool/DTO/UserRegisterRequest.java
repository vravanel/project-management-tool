package com.exercice.project_management_tool.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
    @Schema(description = "Nom utilisateur connecté", example = "vincent") 
    @NotBlank String username,
    @Schema(description = "Adresse email pour se connecter", example = "vincent@test.com") 
    @NotBlank
    @Email
    String email,
    @Schema(description = "Mot de passe", example = "motdepasse123") 
    @NotBlank @Size(min = 8) String password
) {}
