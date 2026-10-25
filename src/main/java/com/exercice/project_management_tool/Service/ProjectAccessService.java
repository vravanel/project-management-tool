package com.exercice.project_management_tool.Service;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.ProjectMemberRepository;
import com.exercice.project_management_tool.Repository.ProjectRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

/**
 * Controôle les droits sur un projet
 * ProjectAccessService
 */
@Service
public class ProjectAccessService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectAccessService(ProjectRepository projectRepository, UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    /**
     * Vérifie que l'utilisateur est membre du projet et si y'a des rôles
     * Sans rôle en paramètre, n'importe quel membre est accepté
     * @param projectId identifiant du projet
     * @param userId identifiant de l'utilisateur
     * @param allowedRoles rôles autorisés (falcultatif)
     */
    public ProjectMember requireRole(Long projectId, Long userId, ProjectMember.Role... allowedRoles) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet introuvable"));
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User introuvable"));
        ProjectMember member = this.projectMemberRepository.findByProjectAndUser(project, user).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas membre de ce projet"));

        if (allowedRoles.length > 0 && !Arrays.asList(allowedRoles).contains(member.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre rôle ne permet pas cette action");
        }
        return member;
    }
}
