package com.exercice.project_management_tool.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.exercice.project_management_tool.Entity.Task;

public record TaskResponse(
        Long id,
        String name,
        String description,
        Task.Priority priority,
        Task.Status status,
        LocalDate dueDate,
        LocalDate endDate,
        UserResponse assignee,
        Long projectId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getEndDate(),
                task.getUser() != null ? UserResponse.from(task.getUser()) : null,
                task.getProject().getId(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
