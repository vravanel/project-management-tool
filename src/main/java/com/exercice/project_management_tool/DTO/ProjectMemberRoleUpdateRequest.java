package com.exercice.project_management_tool.DTO;

import com.exercice.project_management_tool.Entity.ProjectMember;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProjectMemberRoleUpdateRequest(
        @Schema(description = "Nouveau rôle du membre", example = "OBSERVATEUR") @NotNull ProjectMember.Role role) {
}