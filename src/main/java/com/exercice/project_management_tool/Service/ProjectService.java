package com.exercice.project_management_tool.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.DTO.ProjectCreateRequest;
import com.exercice.project_management_tool.DTO.ProjectResponse;
import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.ProjectMemberRepository;
import com.exercice.project_management_tool.Repository.ProjectRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

/**
 * Création et consultation des projets
 * ProjectService
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAccessService projectAccessService;

    ProjectService(ProjectRepository projectRepository, UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository, ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectAccessService = projectAccessService;
    }

    /**
     * Crée le projet et son créateur devient admin du projet
     * @param request données du projet
     * @param userId identifiant de l'utilisateur
     */
    @Transactional 
    public ProjectResponse create(ProjectCreateRequest request, Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Project project = new Project();
        project.setCreator(user);
        project.setDescription(request.description());
        project.setTitle(request.title());
        project.setStartDate(request.startDate());
        ProjectMember projectMember = new ProjectMember();
        projectMember.setRole(ProjectMember.Role.ADMINISTRATEUR);
        projectMember.setProject(project);
        projectMember.setUser(user);
        this.projectRepository.save(project);
        this.projectMemberRepository.save(projectMember);

        return ProjectResponse.from(project);
    }

    /**
     * Renvoie les projets dont l'utilisateur est membre
     * @param userId identifiant de l'utilisateur
     */
    public List<ProjectResponse> findByUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return this.projectMemberRepository.findByUser(user).stream().map(member -> ProjectResponse.from(member.getProject())).toList();
    }

    /**
     * Renvoie un projet uniquement si l'utilisateur est membre
     * @param id identifiant du projet
     * @param userId identifiant de l'utilisateur
     */
    public ProjectResponse findById(Long id, Long userId) {
        this.projectAccessService.requireRole(id, userId);
        Project project = this.projectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ProjectResponse.from(project);
    }
}
