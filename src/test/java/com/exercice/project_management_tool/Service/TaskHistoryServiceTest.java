package com.exercice.project_management_tool.Service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exercice.project_management_tool.TestData;
import com.exercice.project_management_tool.DTO.TaskHistoryResponse;
import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.TaskHistory;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.TaskHistoryRepository;
import com.exercice.project_management_tool.Repository.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TaskHistoryServiceTest {
    @Mock
    private TaskHistoryRepository taskHistoryRepository;
    
    @Mock
    private TaskRepository taskRepository;

    @Mock 
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private TaskHistoryService taskHistoryService;

    private final User user = TestData.user(1L, "vincent@test.com");
    private final Project project = TestData.project(1L, user);
    private final Task task = TestData.task(10L, project, null);

    @Test 
    void findByTask_succes_retournneLHistorique() {
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setFieldName("status");
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        when(taskHistoryRepository.findByTask(task)).thenReturn(List.of(history));

        List<TaskHistoryResponse> result = taskHistoryService.findByTask(10L,1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).fieldName()).isEqualTo("status");
    }

    @Test 
    void save_delegueAuRepository() {
        TaskHistory history = new TaskHistory();
        taskHistoryService.save(history);
        verify(taskHistoryRepository).save(history);
    }
}
