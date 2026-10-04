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

@Service
public class ProjectMemberService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    ProjectMemberService(ProjectRepository projectRepository, UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public ProjectMemberResponse invite(Long projectId, ProjectMemberInviteRequest request) {
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

    public List<ProjectMemberResponse> findByProject(Long projectId) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.projectMemberRepository.findByProject(project).stream().map(ProjectMemberResponse::from).toList();
    }

    public ProjectMemberResponse updateRole(Long memberId, ProjectMemberRoleUpdateRequest request) {
        ProjectMember member = this.projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        member.setRole(request.role());

        return ProjectMemberResponse.from(this.projectMemberRepository.save(member));
    }
}
