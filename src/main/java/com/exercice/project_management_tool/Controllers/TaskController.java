package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.TaskCreateRequest;
import com.exercice.project_management_tool.DTO.TaskResponse;
import com.exercice.project_management_tool.DTO.TaskUpdateRequest;
import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Tâches", description = "Création, mise à jour, consultation et assignation des tâches")
@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Créer une tâche", description = "Crée une tâche avec comme statut A_FAIRE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tâche créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas membre du projet ou est observateur"),
            @ApiResponse(responseCode = "404", description = "Projet ou utilisateur assigné inconnu")
    })
    @PostMapping("/api/project/{projectId}/tasks")
    public TaskResponse create(
            @Parameter(description = "Identifiant du projet", example = "1") @PathVariable Long projectId,
            @RequestBody @Valid TaskCreateRequest request,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.taskService.create(projectId, request, userId);
    }

    @Operation(summary = "Lister les tâches d'un projet", description = "Renvoie toutes les tâches du projet aux membres du projet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des tâches"),
            @ApiResponse(responseCode = "400", description = "Status inconnu"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas membre du projet"),
            @ApiResponse(responseCode = "404", description = "Projet inconnu")
    })
    @GetMapping("/api/project/{projectId}/tasks")
    public List<TaskResponse> findByProject(
            @Parameter(description = "Identifiant du projet", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Filtre facultatif sur le statut", example = "EN_COURS") @RequestParam(required = false) Task.Status status,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        if (status != null) {
            return this.taskService.findByProjectAndStatus(projectId, status, userId);
        }
        return this.taskService.findByProject(projectId, userId);
    }

    @Operation(summary = "Consulter une tâche", description = "Renvoie toutes les informations sur une tâche")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tâche trouvée"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas membre du projet"),
            @ApiResponse(responseCode = "404", description = "Tâche inconnu")
    })
    @GetMapping("/api/tasks/{id}")
    public TaskResponse findById(
            @Parameter(description = "Identifiant de la tâche", example = "1") @PathVariable Long id,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.taskService.findById(id, userId);
    }

    @Operation(summary = "Modifier une tâche", description = "Met à jour toutes les informations de la tâche")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tâche modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas membre du projet"),
            @ApiResponse(responseCode = "404", description = "Tâche ou utilisateur inconnu")
    })
    @PutMapping("/api/tasks/{id}")
    public TaskResponse update(@Parameter(description = "Identifiant de la tâche", example = "1") @PathVariable Long id,
            @RequestBody @Valid TaskUpdateRequest request,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.taskService.update(id, request, userId);
    }
}
