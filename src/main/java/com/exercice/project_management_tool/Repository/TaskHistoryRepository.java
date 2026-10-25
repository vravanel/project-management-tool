package com.exercice.project_management_tool.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.TaskHistory;

/**
 * Accès à l'historique des tâches
 * TaskHistoryRepository
 */
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    /**
     * Historique d'une tâche
     */
    List<TaskHistory> findByTask(Task task);
}
