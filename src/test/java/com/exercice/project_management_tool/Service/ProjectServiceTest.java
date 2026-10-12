package com.exercice.project_management_tool.Service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exercice.project_management_tool.TestData;
import com.exercice.project_management_tool.DTO.ProjectCreateRequest;
import com.exercice.project_management_tool.DTO.ProjectResponse;
import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.ProjectMemberRepository;
import com.exercice.project_management_tool.Repository.ProjectRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private UserRepository userRepository;

    @Mock 
    private ProjectMemberRepository projectMemberRepository;

    @Mock 
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private ProjectService projectService;

    private final User user = TestData.user(1L, "vincent@test.com");
    private final Project project = TestData.project(1L, user);

    @Test 
    void create_succes_creeLeProjetEtLeMembreAdministrateur() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ProjectResponse response = projectService.create(new ProjectCreateRequest("Mon projet", "Une desciption", LocalDate.of(2026,10,5)),1L);

        assertThat(response.title()).isEqualTo("Mon projet");

        ArgumentCaptor<ProjectMember> captor = ArgumentCaptor.forClass(ProjectMember.class);
        verify(projectMemberRepository).save(captor.capture());
        
        assertThat(captor.getValue().getRole()).isEqualTo(ProjectMember.Role.ADMINISTRATEUR);
    }

    @Test
    void findById_succes() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.findById(1L,1L);

        assertThat(response.title()).isEqualTo("Projet de test");
    }
}
