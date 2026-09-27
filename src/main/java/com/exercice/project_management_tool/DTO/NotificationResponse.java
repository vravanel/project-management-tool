package com.exercice.project_management_tool.DTO;

import java.time.LocalDateTime;

import com.exercice.project_management_tool.Entity.Notification;

public record NotificationResponse(
        Long id,
        Long taskId,
        String taskName,
        String type,
        LocalDateTime sentAt,
        Boolean isRead) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTask().getId(),
                notification.getTask().getName(),
                notification.getType(),
                notification.getSentAt(),
                notification.getIsRead());

    }
}
