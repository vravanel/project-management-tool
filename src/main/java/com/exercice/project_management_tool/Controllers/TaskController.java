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

import jakarta.validation.Valid;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/api/project/{projectId}/tasks")
    public TaskResponse create(@PathVariable Long projectId, @RequestBody @Valid TaskCreateRequest request, @RequestParam Long userId) {
        return this.taskService.create(projectId, request, userId);
    }

    @GetMapping("/api/project/{projectId}/tasks")
    public List<TaskResponse> findByProject(@PathVariable Long projectId,
            @RequestParam(required = false) Task.Status status, @RequestParam Long userId) {
        if (status != null) {
            return this.taskService.findByProjectAndStatus(projectId, status, userId);
        }
        return this.taskService.findByProject(projectId, userId);
    }

    @GetMapping("/api/tasks/{id}")
    public TaskResponse findById(@PathVariable Long id, @RequestParam Long userId) {
        return this.taskService.findById(id, userId);
    }

    @PutMapping("/api/tasks/{id}")
    public TaskResponse update(@PathVariable Long id, @RequestBody @Valid TaskUpdateRequest request,
            @RequestParam Long userId) {
        return this.taskService.update(id, request, userId);
    }
}
