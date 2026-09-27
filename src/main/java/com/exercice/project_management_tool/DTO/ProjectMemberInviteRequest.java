package com.exercice.project_management_tool.DTO;

import com.exercice.project_management_tool.Entity.ProjectMember;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectMemberInviteRequest(
    @NotBlank @Email String email,
    @NotNull ProjectMember.Role role
) {}