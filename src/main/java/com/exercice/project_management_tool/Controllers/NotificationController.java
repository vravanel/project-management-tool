package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.NotificationResponse;
import com.exercice.project_management_tool.Service.NotificationService;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/api/users/{userId}/notifications")
    public List<NotificationResponse> findByUser(@PathVariable Long userId) {
        return this.notificationService.findByUser(userId);
    }

    @PutMapping("/api/notifications/{id}/read")
    public NotificationResponse markAsRead(@PathVariable Long id, @RequestParam Long userId) {
        return this.notificationService.markAsRead(id, userId);
    }
}
