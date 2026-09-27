package com.exercice.project_management_tool.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.TaskHistory;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.TaskHistoryRepository;
import com.exercice.project_management_tool.Repository.TaskRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

import jakarta.annotation.Nullable;

@Service
public class TaskHistoryService {

    public final TaskHistoryRepository taskHistoryRepository;
    public final TaskRepository taskRepository;
    public final UserRepository userRepository;

    TaskHistoryService(TaskHistoryRepository taskHistoryRepository, TaskRepository taskRepository,
            UserRepository userRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskHistory create(TaskHistory taskHistory, @Nullable Long userId, Long taskId) {
        if (userId != null) {
            User user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            taskHistory.setUser(user);
        }
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        taskHistory.setTask(task);
        return this.taskHistoryRepository.save(taskHistory);
    }

    public List<TaskHistory> findByTask(Long taskId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return this.taskHistoryRepository.findByTask(task);
    }

}
