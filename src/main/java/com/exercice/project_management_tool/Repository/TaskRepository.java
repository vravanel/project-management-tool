package com.exercice.project_management_tool.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.Task;

/**
 * Accès aux tâches
 * TaskRepository
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
    /**
     * tâche d'un projet
     */
    List<Task> findByProject(Project project);
    /**
     * tâche d'un projet ayant un statut donné
     */
    List<Task> findByProjectAndStatus(Project project, Task.Status status);
}