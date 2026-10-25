package com.exercice.project_management_tool.DTO;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
        @Schema(description = "Titre du projet (2 à 100 caractères)", example = "Test") @NotBlank @Size(min = 2, max = 100) String title,
        @Schema(description = "Description du projet (5 caractères minimum)", example = "Description test") @NotBlank @Size(min = 5) String description,
        @Schema(description = "Date de début au format AAAA-MM-JJ", example = "2026-10-05") @NotNull LocalDate startDate) {
}
