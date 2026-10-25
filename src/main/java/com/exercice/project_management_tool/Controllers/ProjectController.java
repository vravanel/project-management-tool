package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.ProjectCreateRequest;
import com.exercice.project_management_tool.DTO.ProjectResponse;
import com.exercice.project_management_tool.Service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Projets", description = "Création et consultation des projets")
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    public final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Lister mes projets", description = "Renvoie uniquement les projets dont l'utilisateur est membre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des projets"),
            @ApiResponse(responseCode = "404", description = "Utilisateur inconnu")
    })
    @GetMapping()
    public List<ProjectResponse> findByUser(
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.projectService.findByUser(userId);
    }

    @Operation(summary = "Créer un projet", description = "Crée le projet et assigne le créateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projet créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Utilisateur inconnu")
    })
    @PostMapping("/{userId}")
    public ProjectResponse create(
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @PathVariable Long userId,
            @RequestBody @Valid ProjectCreateRequest request) {
        return this.projectService.create(request, userId);
    }

    @Operation(summary = "Consulter un projet", description = "Réservé aux membres du projet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projet trouvé"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas membre de ce projet"),
            @ApiResponse(responseCode = "404", description = "Projet ou utilisateur inconnu")
    })
    @GetMapping("/{id}")
    public ProjectResponse findById(
            @Parameter(description = "Identifiant du projet", example = "1") @PathVariable Long id,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.projectService.findById(id, userId);
    }

}
