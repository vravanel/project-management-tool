package com.exercice.project_management_tool.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
    @NotBlank @Size(min = 2, max = 100) String title,
    @NotBlank @Size(min = 5) String description,
    @NotNull LocalDate startDate
) {
    
}
