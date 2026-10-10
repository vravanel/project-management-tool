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

    public TaskResponse findById(Long id, Long requesterId) {
        Task task = this.findEntityById(id);
        this.projectAccessService.requireRole(task.getProject().getId(), requesterId);
        return TaskResponse.from(task);
    }

    public List<TaskResponse> findByProject(Long projectId, Long requesterId) {
        this.projectAccessService.requireRole(projectId, requesterId);
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.taskRepository.findByProject(project).stream().map(TaskResponse::from).toList();
    }

    public List<TaskResponse> findByProjectAndStatus(Long projectId, Task.Status status, Long requesterId) {
        this.projectAccessService.requireRole(projectId, requesterId);
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.taskRepository.findByProjectAndStatus(project, status).stream().map(TaskResponse::from).toList();
    }

    private Task findEntityById(Long id) {
        return this.taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private User findAssignee(Project project, Long assigneeId) {
        User assignee = this.userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assigné introuvable"));
        this.projectMemberRepository.findByProjectAndUser(project, assignee).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'assigné n'est pas membre du projet"));
        return assignee;
    }

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
