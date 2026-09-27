package com.exercice.project_management_tool.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.ProjectRepository;
import com.exercice.project_management_tool.Repository.TaskRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

@Service
public class TaskService {

    public final TaskRepository taskRepository;
    public final UserRepository userRepository;
    public final ProjectRepository projectRepository;

    TaskService(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task create(Task task, Long userId, Long projectId) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (userId != null) {
            User user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            task.setUser(user);
        }

        task.setProject(project);
        return this.taskRepository.save(task);
    }

    public Task findById(Long id) {
        return this.taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Task> findAll() {
        return this.taskRepository.findAll();
    }

    public List<Task> findByStatus(Task.Status status) {
        return this.taskRepository.findByStatus(status);
    }

    public Task update(Long id, Task task) {
        Task newTask = this.taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        newTask.setDescription(task.getDescription());
        newTask.setDueDate(task.getDueDate());
        newTask.setEndDate(task.getEndDate());
        newTask.setName(task.getName());
        newTask.setPriority(task.getPriority());
        newTask.setStatus(task.getStatus());
        newTask.setUser(task.getUser());
        return this.taskRepository.save(newTask);
    }
}
