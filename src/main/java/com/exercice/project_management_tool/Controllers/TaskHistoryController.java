package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.TaskHistoryResponse;
import com.exercice.project_management_tool.Service.TaskHistoryService;

@RestController 
@RequestMapping("/api/tasks/{taskId}/history")
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;

    public TaskHistoryController(TaskHistoryService taskHistoryService) {
        this.taskHistoryService = taskHistoryService;
    }

    @GetMapping()
    public List<TaskHistoryResponse> findByTask(@PathVariable Long taskId, @RequestParam Long userId) {
        return this.taskHistoryService.findByTask(taskId, userId);
    }
    
}
