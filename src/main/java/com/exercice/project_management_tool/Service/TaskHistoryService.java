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

/**
 * Consultation et enregistrement de l'historique des modifications d'une tâche
 * TaskHistoryService
 */
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

    /**
     * Liste l'historique d'une tâche uniquement pour les membres du projet
     * @param taskId Identifiant de la tâche
     * @param requesterId identifiant de l'utilisateur qui consulte
     */
    public List<TaskHistoryResponse> findByTask(Long taskId, Long requesterId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.projectAccessService.requireRole(task.getProject().getId(), requesterId);
        return this.taskHistoryRepository.findByTask(task).stream().map(TaskHistoryResponse::from).toList();
    }

    /**
     * Enregistre un historique
     * @param history la ligne d'historique a enregistré
     */
    public void save(TaskHistory history) {
        this.taskHistoryRepository.save(history);
    }

}
