package com.exercice.project_management_tool.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.DTO.TaskHistoryResponse;
import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.TaskHistory;
import com.exercice.project_management_tool.Repository.TaskHistoryRepository;
import com.exercice.project_management_tool.Repository.TaskRepository;

@Service
public class TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskRepository taskRepository;
    private final ProjectAccessService projectAccessService;

    TaskHistoryService(TaskHistoryRepository taskHistoryRepository, TaskRepository taskRepository,
            ProjectAccessService projectAccessService) {
        this.taskHistoryRepository = taskHistoryRepository;
        this.taskRepository = taskRepository;
        this.projectAccessService = projectAccessService;
    }

    public List<TaskHistoryResponse> findByTask(Long taskId, Long requesterId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.projectAccessService.requireRole(task.getProject().getId(), requesterId);
        return this.taskHistoryRepository.findByTask(task).stream().map(TaskHistoryResponse::from).toList();
    }

    public void save(TaskHistory history) {
        this.taskHistoryRepository.save(history);
    }

}
