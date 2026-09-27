package com.exercice.project_management_tool.DTO;

import java.time.LocalDate;

import com.exercice.project_management_tool.Entity.Task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskCreateRequest(
    @NotBlank @Size(min = 2, max = 100) String name,
    @NotBlank @Size(min = 5) String description,
    @NotNull Task.Priority priority, 
    @NotNull LocalDate dueDate,
    Long assigneeId
) {} 
