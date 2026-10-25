package com.exercice.project_management_tool.DTO;

import com.exercice.project_management_tool.Entity.ProjectMember;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectMemberInviteRequest(
        @Schema(description = "Adresse email d'un utilisateur déjà inscrit", example = "vincent@test.com") @NotBlank @Email String email,
        @Schema(description = "Rôle attribué dans le projet", example = "MEMBRE") @NotNull ProjectMember.Role role) {
}