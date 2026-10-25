package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.TaskHistoryResponse;
import com.exercice.project_management_tool.Service.TaskHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Historique des tâches", description = "Suivi des modifications des tâches")
@RestController
@RequestMapping("/api/tasks/{taskId}/history")
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;

    public TaskHistoryController(TaskHistoryService taskHistoryService) {
        this.taskHistoryService = taskHistoryService;
    }

    @Operation(summary = "Consulter l'historique d'une tâche", description = "Liste les modifications de la tâche")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique de la tâche"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas membre du projet"),
            @ApiResponse(responseCode = "404", description = "Tâche inconnu")
    })
    @GetMapping()
    public List<TaskHistoryResponse> findByTask(
            @Parameter(description = "Identifiant de la tâche", example = "1") @PathVariable Long taskId,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.taskHistoryService.findByTask(taskId, userId);
    }

}
