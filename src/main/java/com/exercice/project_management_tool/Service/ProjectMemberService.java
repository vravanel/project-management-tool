package com.exercice.project_management_tool.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.DTO.ProjectMemberInviteRequest;
import com.exercice.project_management_tool.DTO.ProjectMemberResponse;
import com.exercice.project_management_tool.DTO.ProjectMemberRoleUpdateRequest;
import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.ProjectMemberRepository;
import com.exercice.project_management_tool.Repository.ProjectRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

/**
 * Gestion des membres d'un projet et de leurs rôles
 * ProjectMemberService
 */
@Service
public class ProjectMemberService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAccessService projectAccessService;

    ProjectMemberService(ProjectRepository projectRepository, UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository, ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectAccessService = projectAccessService;
    }

    /**
     * Ajoute un utilisateur au projet avec un rôle. Uniquement pour les admins
     * @param projectId identifiant projet
     * @param request email de l'utilisateur à inviter et rôle à lui donner
     * @param requesterId identifiant de l'admin qui l'invite
     */
    public ProjectMemberResponse invite(Long projectId, ProjectMemberInviteRequest request, Long requesterId) {
        this.projectAccessService.requireRole(projectId, requesterId, ProjectMember.Role.ADMINISTRATEUR);

        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User user = this.userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        this.projectMemberRepository.findByProjectAndUser(project, user).ifPresent(m -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Déjà membre du projet");
        });

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(request.role());

        return ProjectMemberResponse.from(this.projectMemberRepository.save(member));
    }

    /**
     * Liste les membres d'un projet uniquement pour les membres du projet
     * @param projectId identifiant du projet
     * @param requesterId identifiant de l'utilisateur qui consulte
     */
    public List<ProjectMemberResponse> findByProject(Long projectId, Long requesterId) {
        this.projectAccessService.requireRole(projectId, requesterId);

        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.projectMemberRepository.findByProject(project).stream().map(ProjectMemberResponse::from).toList();
    }

    /**
     * Change le rôle d'un membre uniquement pour les admins
     * @param memberId identifiant du membre du projet
     * @param request nouveau rôle
     * @param projectId identifiant du projet
     * @param requesterId identifiant de l'admin qui modifie
     */
    public ProjectMemberResponse updateRole(Long memberId, ProjectMemberRoleUpdateRequest request, Long projectId,
            Long requesterId) {
        this.projectAccessService.requireRole(projectId, requesterId, ProjectMember.Role.ADMINISTRATEUR);

        ProjectMember member = this.projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!member.getProject().getId().equals(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce membre n'appartient pas à ce projet");
        }

        member.setRole(request.role());

        return ProjectMemberResponse.from(this.projectMemberRepository.save(member));
    }
}
