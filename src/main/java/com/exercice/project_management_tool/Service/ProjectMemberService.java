package com.exercice.project_management_tool.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public ProjectMember create(ProjectMember projectMember, Long userId, Long projectId) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        projectMember.setUser(user);
        projectMember.setProject(project);
        return this.projectMemberRepository.save(projectMember);
    }

    public List<ProjectMember> findAll() {
        return this.projectMemberRepository.findAll();
    }

    public ProjectMember updateRole(ProjectMember projectMember, Long id) {
        ProjectMember newProjectMember = this.projectMemberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        newProjectMember.setRole(projectMember.getRole());
        return this.projectMemberRepository.save(newProjectMember);
    }
}
