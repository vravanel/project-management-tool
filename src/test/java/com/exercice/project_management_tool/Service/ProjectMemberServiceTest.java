package com.exercice.project_management_tool.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.exercice.project_management_tool.HttpAssertions.assertStatus;

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
import com.exercice.project_management_tool.DTO.ProjectMemberInviteRequest;
import com.exercice.project_management_tool.DTO.ProjectMemberResponse;
import com.exercice.project_management_tool.DTO.ProjectMemberRoleUpdateRequest;
import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.ProjectMemberRepository;
import com.exercice.project_management_tool.Repository.ProjectRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ProjectMemberServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private ProjectMemberService projectMemberService;

    private final User invite = TestData.user(1L, "invite@test.com");
    private final User admin = TestData.user(2L, "admin@test.com");
    private final Project project = TestData.project(1L, admin);

    @Test
    void invite_pasAdmin_leve403() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN)).when(projectAccessService).requireRole(1L, 9L,
                ProjectMember.Role.ADMINISTRATEUR);

        assertStatus(
                () -> projectMemberService.invite(1L,
                        new ProjectMemberInviteRequest("invite@test.com", ProjectMember.Role.MEMBRE), 9L),
                HttpStatus.FORBIDDEN);
        verify(projectMemberRepository,never()).save(any(ProjectMember.class));
    }

    @Test
    void invite_emailInconnu_leve404() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

        assertStatus(
                () -> projectMemberService.invite(1L,
                        new ProjectMemberInviteRequest("inconnu@test.com", ProjectMember.Role.MEMBRE), 1L),
                HttpStatus.NOT_FOUND);
    }

    @Test
    void invite_dejaMembre_leve409() {
        ProjectMember existant = TestData.member(5L, invite, project, ProjectMember.Role.MEMBRE);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("invite@test.com")).thenReturn(Optional.of(invite));
        when(projectMemberRepository.findByProjectAndUser(project, invite)).thenReturn(Optional.of(existant));

        assertStatus(
                () -> projectMemberService.invite(1L,
                        new ProjectMemberInviteRequest("invite@test.com", ProjectMember.Role.MEMBRE), 1L),
                HttpStatus.CONFLICT);
        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    void invite_sucess_creeLeMembreAvecLeRoleDemande() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("invite@test.com")).thenReturn(Optional.of(invite));
        when(projectMemberRepository.findByProjectAndUser(project, invite)).thenReturn(Optional.empty());
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjectMemberResponse response = projectMemberService.invite(1L,
                new ProjectMemberInviteRequest("invite@test.com", ProjectMember.Role.OBSERVATEUR), 1L);

        assertThat(response.role()).isEqualTo(ProjectMember.Role.OBSERVATEUR);
        assertThat(response.user().email()).isEqualTo("invite@test.com");
        assertThat(response.projectId()).isEqualTo(1L);
    }

    @Test
    void findByProject_retourneLesMembres() {
        ProjectMember membre = TestData.member(1L, admin, project, ProjectMember.Role.ADMINISTRATEUR);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProject(project)).thenReturn(List.of(membre));

        List<ProjectMemberResponse> result = projectMemberService.findByProject(1L, 1L);
        assertThat(result).hasSize(1);

        assertThat(result.get(0).role()).isEqualTo(ProjectMember.Role.ADMINISTRATEUR);
    }

    @Test
    void findByProject_projetInconnu_leve404() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertStatus(() -> projectMemberService.findByProject(1L, 1L), HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRole_pasAdmin_leve403() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN)).when(projectAccessService).requireRole(1L, 9L,
                ProjectMember.Role.ADMINISTRATEUR);

        assertStatus(() -> projectMemberService.updateRole(5L,
                new ProjectMemberRoleUpdateRequest(ProjectMember.Role.MEMBRE), 1L, 9L), HttpStatus.FORBIDDEN);
    }

    @Test
    void updateRole_membreInconnu_leve404() {
        when(projectMemberRepository.findById(5L)).thenReturn(Optional.empty());

        assertStatus(() -> projectMemberService.updateRole(5L,
                new ProjectMemberRoleUpdateRequest(ProjectMember.Role.MEMBRE), 1L, 1L), HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRole_membreUnAutreProjet_leve404() {
        Project autreProjet = TestData.project(2L, admin);
        ProjectMember membre = TestData.member(5L, invite, autreProjet, ProjectMember.Role.MEMBRE);

        when(projectMemberRepository.findById(5L)).thenReturn(Optional.of(membre));

        assertStatus(
                () -> projectMemberService.updateRole(5L,
                        new ProjectMemberRoleUpdateRequest(ProjectMember.Role.ADMINISTRATEUR), 1L, 1L),
                HttpStatus.NOT_FOUND);
        verify(projectMemberRepository,never()).save(any(ProjectMember.class));
    }

    @Test
    void updateRole_suces_changeLeRole() {
        ProjectMember membre = TestData.member(5L, invite, project, ProjectMember.Role.MEMBRE);
        when(projectMemberRepository.findById(5L)).thenReturn(Optional.of(membre));

        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjectMemberResponse response = projectMemberService.updateRole(5L,
                new ProjectMemberRoleUpdateRequest(ProjectMember.Role.ADMINISTRATEUR), 1L, 1L);

        assertThat(response.role()).isEqualTo(ProjectMember.Role.ADMINISTRATEUR);
    }
}
