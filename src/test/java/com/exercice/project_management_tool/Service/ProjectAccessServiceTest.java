package com.exercice.project_management_tool.Service;

import static com.exercice.project_management_tool.HttpAssertions.assertStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.exercice.project_management_tool.TestData;
import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.ProjectMemberRepository;
import com.exercice.project_management_tool.Repository.ProjectRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ProjectAccessServiceTest {
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private UserRepository userRepository;

    @Mock 
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks 
    private ProjectAccessService projectAccessService;

    private final User user = TestData.user(1L, "vincent@test.com");
    private final Project project = TestData.project(1L, user);

    @Test 
    void requireRole_projectInconnu_leve404() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertStatus(() -> projectAccessService.requireRole(1L,2L), HttpStatus.NOT_FOUND);
    }

    @Test
    void requireRole_nonMembre_leve403() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(projectMemberRepository.findByProjectAndUser(project, user)).thenReturn(Optional.empty());

        assertStatus(() -> projectAccessService.requireRole(1L,2L), HttpStatus.FORBIDDEN);
    }

    @Test 
    void requireRole_roleNonAutorise_leve403() {
        ProjectMember observateur = TestData.member(1L, user, project, ProjectMember.Role.OBSERVATEUR);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(projectMemberRepository.findByProjectAndUser(project, user)).thenReturn(Optional.of(observateur));

        assertStatus(() -> projectAccessService.requireRole(1L,2L, ProjectMember.Role.ADMINISTRATEUR, ProjectMember.Role.MEMBRE), HttpStatus.FORBIDDEN);
    }

    @Test 
    void requireRole_roleAutorise_retourneLeMembre() {
        ProjectMember membre = TestData.member(1L, user, project, ProjectMember.Role.MEMBRE);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(projectMemberRepository.findByProjectAndUser(project, user)).thenReturn(Optional.of(membre));

        ProjectMember result = projectAccessService.requireRole(1L,2L, ProjectMember.Role.ADMINISTRATEUR, ProjectMember.Role.MEMBRE);

        assertThat(result).isSameAs(membre);
    }

    @Test 
    void requireRole_sansRoleDemande_toutMembreEstAccepte() {
          ProjectMember observateur = TestData.member(1L, user, project, ProjectMember.Role.OBSERVATEUR);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(projectMemberRepository.findByProjectAndUser(project, user)).thenReturn(Optional.of(observateur));

        ProjectMember result = projectAccessService.requireRole(1L,2L);

        assertThat(result.getRole()).isEqualTo(ProjectMember.Role.OBSERVATEUR);
    }

    @Test 
    void requireRole_utilisateurInconnu_leve404() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertStatus(() -> projectAccessService.requireRole(1L, 2L), HttpStatus.NOT_FOUND);
    }
}
