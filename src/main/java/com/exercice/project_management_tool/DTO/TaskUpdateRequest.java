package com.exercice.project_management_tool.DTO;

import java.time.LocalDate;

import com.exercice.project_management_tool.Entity.Task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskUpdateRequest(
    @Schema(description = "Nom de la tâche (2 à 100 caractères)", example = "Nom de la tâche")
    @NotBlank @Size(min = 2, max = 100) String name,
    @Schema(description = "Description de la tâche (5 caractères minimum)", example = "Description de la tâche")
    @NotBlank @Size(min = 5) String description,
    @Schema(description = "Priorité de la tâche", example = "HAUTE")
    @NotNull Task.Priority priority, 
    @Schema(description = "Status de la tâche", example = "A_FAIRE")
    @NotNull Task.Status status,
    @Schema(description = "Date d'échéance au format AAAA-MM-JJ", example = "2026-11-01")
    @NotNull LocalDate dueDate,
    @Schema(description = "Date de fin au format AAAA-MM-JJ", example = "2026-11-01")
    LocalDate endDate,
    @Schema(description = "Identifiant de l'utilisateur assignée", example = "1")
    Long assigneeId
) {} 
