package com.exercice.project_management_tool.DTO;

import com.exercice.project_management_tool.Entity.ProjectMember;

public record ProjectMemberResponse(
    Long id,
    UserResponse user,
    Long projectId,
    ProjectMember.Role role
) 
{
    public static ProjectMemberResponse from(ProjectMember member) {
        return new ProjectMemberResponse(
            member.getId(),
            UserResponse.from(member.getUser()),
            member.getProject().getId(),
            member.getRole()
        );
    }
} 