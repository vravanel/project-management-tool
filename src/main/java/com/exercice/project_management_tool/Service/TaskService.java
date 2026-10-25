package com.exercice.project_management_tool.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.DTO.TaskCreateRequest;
import com.exercice.project_management_tool.DTO.TaskResponse;
import com.exercice.project_management_tool.DTO.TaskUpdateRequest;
import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.TaskHistory;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.ProjectMemberRepository;
import com.exercice.project_management_tool.Repository.ProjectRepository;
import com.exercice.project_management_tool.Repository.TaskRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

/**
 * Gestion des tâches (création, consultation, modification avec historique et notification)
 * TaskService
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskHistoryService taskHistoryService;
    private final NotificationService notificationService;
    private final ProjectAccessService projectAccessService;
    private final ProjectMemberRepository projectMemberRepository;

    TaskService(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository,
            TaskHistoryService taskHistoryService, NotificationService notificationService,
            ProjectAccessService projectAccessService, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskHistoryService = taskHistoryService;
        this.notificationService = notificationService;
        this.projectAccessService = projectAccessService;
        this.projectMemberRepository = projectMemberRepository;
    }

    /**
     * Crée une tâche au statut A_FAIRE. Réservé aux admins et membres
     * @param projectId identifiant du projet
     * @param request données de la tâche
     * @param requesterId identifiant de l'utilisateur qui crée la tâche
     */
    public TaskResponse create(Long projectId, TaskCreateRequest request, Long requesterId) {
        this.projectAccessService.requireRole(projectId, requesterId, ProjectMember.Role.ADMINISTRATEUR,
                ProjectMember.Role.MEMBRE);

        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Task task = new Task();
        task.setName(request.name());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setStatus(Task.Status.A_FAIRE);
        task.setProject(project);

        if (request.assigneeId() != null) {
            task.setUser(this.findAssignee(project, request.assigneeId()));
        }

        Task saved = this.taskRepository.save(task);

        if (saved.getUser() != null) {
            this.notificationService.notifyAssignment(saved);
        }

        return TaskResponse.from(saved);
    }

    /**
     * Renvoie une tâche uniquement pour les membres. 
     * @param id identifiant de la tâche
     * @param requesterId identifiant de l'utilisateur qui consulte
     */
    public TaskResponse findById(Long id, Long requesterId) {
        Task task = this.findEntityById(id);
        this.projectAccessService.requireRole(task.getProject().getId(), requesterId);
        return TaskResponse.from(task);
    }

    /**
     * Renvoie toutes les tâches d'un projet uniquement pour les membres du projet
     * @param projectId identifiant du projet
     * @param requesterId identifiant de l'utilisateur qui consulte
     */
    public List<TaskResponse> findByProject(Long projectId, Long requesterId) {
        this.projectAccessService.requireRole(projectId, requesterId);
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.taskRepository.findByProject(project).stream().map(TaskResponse::from).toList();
    }

    /**
     * Renvoie les tâches d'un projet qui ont un statut donné
     * @param projectId identifiant du projet
     * @param status statut recherché
     * @param requesterId identifiant de l'utilisateur qui consulte
     */
    public List<TaskResponse> findByProjectAndStatus(Long projectId, Task.Status status, Long requesterId) {
        this.projectAccessService.requireRole(projectId, requesterId);
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.taskRepository.findByProjectAndStatus(project, status).stream().map(TaskResponse::from).toList();
    }

    /**
     * Retrouve une tâche
     * @param id identifiant de la tâche
     */
    private Task findEntityById(Long id) {
        return this.taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrouve l'utilisateur assigné et vérifie qu'il est bien membre du projet
     * @param project le projet de la tâche
     * @param assigneeId identifiant de l'utilisateur à assigner
     */
    private User findAssignee(Project project, Long assigneeId) {
        User assignee = this.userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assigné introuvable"));
        this.projectMemberRepository.findByProjectAndUser(project, assignee).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'assigné n'est pas membre du projet"));
        return assignee;
    }

    /**
     * Ajoute une ligne à la liste d'historique si la valeur change
     * @param changes liste des lignes d'historique en cours de préparation
     * @param task la tâche modifiée
     * @param editor l'utilisateur qui fait la modification
     * @param field nom du champ modifié
     * @param oldValue valeur actuelle 
     * @param newValue nouvelle valeur
     */
    private void trackChange(List<TaskHistory> changes, Task task, User editor, String field, Object oldValue,
            Object newValue) {

        if (!Objects.equals(oldValue, newValue)) {
            TaskHistory history = new TaskHistory();
            history.setTask(task);
            history.setUser(editor);
            history.setFieldName(field);
            history.setFieldValueOld(oldValue != null ? oldValue.toString() : null);
            history.setFieldValueNew(newValue != null ? newValue.toString() : null);
            changes.add(history);
        }
    }

    /**
     * Modifie une tâche uniquement pour les membres du projet
     * @param id identifiant de la tâche
     * @param request nouvelles valeurs de la tâche
     * @param requesterId identifiant de l'utilisateur qui modifie
     */
    public TaskResponse update(Long id, TaskUpdateRequest request, Long requesterId) {
        Task task = this.findEntityById(id);
        ProjectMember editorMember = this.projectAccessService.requireRole(task.getProject().getId(), requesterId,
                ProjectMember.Role.ADMINISTRATEUR, ProjectMember.Role.MEMBRE);

        User editor = editorMember.getUser();

        User newAssignee = request.assigneeId() != null ? this.findAssignee(task.getProject(), request.assigneeId())
                : null;

        List<TaskHistory> changes = new ArrayList<>();
        Long oldAssigneeId = task.getUser() != null ? task.getUser().getId() : null;

        this.trackChange(changes, task, editor, "name", task.getName(), request.name());
        this.trackChange(changes, task, editor, "description", task.getDescription(), request.description());
        this.trackChange(changes, task, editor, "priority", task.getPriority(), request.priority());
        this.trackChange(changes, task, editor, "status", task.getStatus(), request.status());
        this.trackChange(changes, task, editor, "dueDate", task.getDueDate(), request.dueDate());
        this.trackChange(changes, task, editor, "endDate", task.getEndDate(), request.endDate());
        this.trackChange(changes, task, editor, "assignee", oldAssigneeId, request.assigneeId());

        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setEndDate(request.endDate());
        task.setName(request.name());
        task.setPriority(request.priority());
        task.setStatus(request.status());
        task.setUser(newAssignee);

        Task saved = this.taskRepository.save(task);
        changes.forEach(this.taskHistoryService::save);

        boolean assigneeChanged = !Objects.equals(oldAssigneeId, request.assigneeId());

        if (assigneeChanged && saved.getUser() != null) {
            this.notificationService.notifyAssignment(saved);
        }

        return TaskResponse.from(saved);
    }
}
