package com.exercice.project_management_tool.DTO;

import com.exercice.project_management_tool.Entity.ProjectMember;

import jakarta.validation.constraints.NotNull;

public record ProjectMemberRoleUpdateRequest(
    @NotNull ProjectMember.Role role
) 
{} 