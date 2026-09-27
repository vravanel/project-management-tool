package com.exercice.project_management_tool.DTO;

import java.time.LocalDateTime;

import com.exercice.project_management_tool.Entity.TaskHistory;

public record TaskHistoryResponse(
        Long id,
        String fieldName,
        String fieldValueOld,
        String fieldValueNew,
        LocalDateTime modifiedAt,
        UserResponse user) {
    public static TaskHistoryResponse from(TaskHistory history) {
        return new TaskHistoryResponse(
                history.getId(),
                history.getFieldName(),
                history.getFieldValueOld(),
                history.getFieldValueNew(),
                history.getModifiedAt(),
                history.getUser() != null ? UserResponse.from(history.getUser()) : null);
    }
}
