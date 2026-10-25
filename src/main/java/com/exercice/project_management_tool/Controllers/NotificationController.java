package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.NotificationResponse;
import com.exercice.project_management_tool.Service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notifications", description = "Notifications reçues par un utilisateur quand une tâche lui est attachée")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Lister les notfications d'un utilisateur", description = "Renvoie les notifcations de l'utilisateur + envoie un email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des notifications"),
            @ApiResponse(responseCode = "404", description = "Utilisateur inconnu")
    })
    @GetMapping("/api/users/{userId}/notifications")
    public List<NotificationResponse> findByUser(
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @PathVariable Long userId) {
        return this.notificationService.findByUser(userId);
    }

    @Operation(summary = "Marquer une notification comme lue", description = "L'utilisateur peut marquer la notification comme lue")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification marquée comme lue"),
            @ApiResponse(responseCode = "403", description = "La notification n'appartient pas à l'utilisateur"),
            @ApiResponse(responseCode = "404", description = "Notification inconnu")
    })
    @PutMapping("/api/notifications/{id}/read")
    public NotificationResponse markAsRead(
            @Parameter(description = "Identifiant de la notification", example = "1") @PathVariable Long id,
            @Parameter(description = "Identifiant de l'utilisateur qui requête", example = "1") @RequestParam Long userId) {
        return this.notificationService.markAsRead(id, userId);
    }
}
