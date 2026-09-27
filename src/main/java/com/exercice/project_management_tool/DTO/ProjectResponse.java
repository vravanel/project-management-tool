package com.exercice.project_management_tool.DTO;

import java.time.LocalDate;

import com.exercice.project_management_tool.Entity.Project;

public record ProjectResponse(
    Long id,
    String title,
    String description,
    LocalDate startDate,
    UserResponse creator
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getTitle(),
            project.getDescription(),
            project.getStartDate(),
            UserResponse.from(project.getCreator())
        );
    }
}
