package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.ProjectMemberInviteRequest;
import com.exercice.project_management_tool.DTO.ProjectMemberResponse;
import com.exercice.project_management_tool.DTO.ProjectMemberRoleUpdateRequest;
import com.exercice.project_management_tool.Service.ProjectMemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Membres des projets", description = "Invitation des membres et gestion de leurs rôles")
@RestController
@RequestMapping("/api/project/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @Operation(summary = "Inviter un membre", description = "Ajoute au projet un utilisateur uniquement par administrateur du projet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membre ajouté"),
            @ApiResponse(responseCode = "404", description = "Projet inconnu ou aucun utilisateur"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas administrateur"),
            @ApiResponse(responseCode = "409", description = "Cet utilisateur est déjà membre du projet")
    })
    @PostMapping()
    public ProjectMemberResponse invite(
            @Parameter(description = "Identifiant du projet", example = "1") @PathVariable Long projectId,
            @RequestBody @Valid ProjectMemberInviteRequest request,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.projectMemberService.invite(projectId, request, userId);
    }

    @Operation(summary = "Lister les membres d'un projet", description = "Réservé aux membres du projet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des membres avec leurs rôles"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas membre du projet"),
            @ApiResponse(responseCode = "404", description = "Projet inconnu")
    })
    @GetMapping()
    public List<ProjectMemberResponse> findByProject(
            @Parameter(description = "Identifiant de projet", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.projectMemberService.findByProject(projectId, userId);
    }

    @Operation(summary = "Changer le rôle d'un membre", description = "Modifie le rôle d'un membre du projet uniquement par admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rôle modifié"),
            @ApiResponse(responseCode = "400", description = "Rôle manquant ou invalide"),
            @ApiResponse(responseCode = "404", description = "Projet inconnu ou membre n'appartenant pas au projet"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas admin du projet")

    })
    @PutMapping("/{memberId}")
    public ProjectMemberResponse updateRole(
            @Parameter(description = "Identifiant de projet", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Identifiant d'un membre du projet", example = "1") @PathVariable Long memberId,
            @RequestBody @Valid ProjectMemberRoleUpdateRequest request,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.projectMemberService.updateRole(memberId, request, projectId, userId);
    }
}
