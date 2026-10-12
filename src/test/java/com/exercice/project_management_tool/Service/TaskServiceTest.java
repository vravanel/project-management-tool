package com.exercice.project_management_tool.Service;

import static com.exercice.project_management_tool.HttpAssertions.assertStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.TestData;
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

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private TaskHistoryService taskHistoryService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private TaskService taskService;

    private final User admin = TestData.user(1L, "admin@test.com");
    private final User test = TestData.user(2L, "test@test.com");
    private final Project project = TestData.project(1L, admin);
    private final ProjectMember adminMember = TestData.member(1L, admin, project, ProjectMember.Role.ADMINISTRATEUR);

    private void testEstMembre() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(test));
        when(projectMemberRepository.findByProjectAndUser(project, test))
                .thenReturn(Optional.of(TestData.member(2L, test, project, ProjectMember.Role.MEMBRE)));
    }

    @Test
    void create_observateur_leve403() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN)).when(projectAccessService).requireRole(1L, 7L,
                ProjectMember.Role.ADMINISTRATEUR, ProjectMember.Role.MEMBRE);

        assertStatus(() -> taskService.create(1L,
                new TaskCreateRequest("Tâche", "Une description", Task.Priority.HAUTE, LocalDate.of(2026, 12, 1), null),
                7L), HttpStatus.FORBIDDEN);
    }

    @Test
    void create_avecAssigneMembre_notifieLAssigne() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        testEstMembre();
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = taskService.create(1L,
                new TaskCreateRequest("Tâche", "Une description", Task.Priority.HAUTE, LocalDate.of(2026, 12, 1), 2L),
                1L);

        assertThat(response.status()).isEqualTo(Task.Status.A_FAIRE);
        assertThat(response.assignee().email()).isEqualTo("test@test.com");
        verify(notificationService).notifyAssignment(any(Task.class));
    }

    @Test
    void update_seulLeStatutChange_uneSeuleEntreeHistorique() {
        Task task = TestData.task(10L, project, null);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(projectAccessService.requireRole(1L, 1L, ProjectMember.Role.ADMINISTRATEUR, ProjectMember.Role.MEMBRE)).thenReturn(adminMember);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = taskService.update(10L,
                new TaskUpdateRequest(TestData.TASK_NAME, TestData.TASK_DESCRIPTION, Task.Priority.MOYENNE,
                        Task.Status.EN_COURS, TestData.TASK_DUE_DATE, null, null),
                1L);

        assertThat(response.status()).isEqualTo(Task.Status.EN_COURS);
        verify(taskHistoryService, times(1)).save(any(TaskHistory.class));
        verify(notificationService, never()).notifyAssignment(any(Task.class));
    }

    @Test
    void update_changementAssigne_historiqueEtNotification() {
        Task task = TestData.task(10L, project, null);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(projectAccessService.requireRole(1L,1L, ProjectMember.Role.ADMINISTRATEUR, ProjectMember.Role.MEMBRE)).thenReturn(adminMember);

        testEstMembre();
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        taskService.update(10L, new TaskUpdateRequest(TestData.TASK_NAME, TestData.TASK_DESCRIPTION, Task.Priority.MOYENNE, Task.Status.A_FAIRE, TestData.TASK_DUE_DATE, null, 2L), 1L);
        
        verify(taskHistoryService, times(1)).save(any(TaskHistory.class));
        verify(notificationService).notifyAssignment(any(Task.class));
    }

    @Test
    void findById_succes() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(TestData.task(10L, project, null)));
        assertThat(taskService.findById(10L, 1L).id()).isEqualTo(10L);
    }

    @Test
    void findByProject_retourneLesTaches() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.findByProject(project)).thenReturn(List.of(TestData.task(10L, project, null)));

        assertThat(taskService.findByProject(1L, 1L)).hasSize(1);
    }

    @Test 
    void findByProjectAndStatus_retourneLesTachesDuStatut() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectAndStatus(project, Task.Status.EN_COURS)).thenReturn(List.of(TestData.task(10L, project, null)));

        assertThat(taskService.findByProjectAndStatus(1L, Task.Status.EN_COURS, 1L)).hasSize(1);
    }
}
