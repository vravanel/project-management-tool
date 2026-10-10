package com.exercice.project_management_tool.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByProjectAndStatus(Project project, Task.Status status);
}