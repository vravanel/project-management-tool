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
public class ProjectService {

    public final ProjectRepository projectRepository;
    public final UserRepository userRepository;
    public final ProjectMemberRepository projectMemberRepository;

    ProjectService(ProjectRepository projectRepository, UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public Project create(Project project, Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        project.setCreator(user);
        ProjectMember projectMember = new ProjectMember();
        projectMember.setRole(ProjectMember.Role.ADMINISTRATEUR);
        projectMember.setProject(project);
        projectMember.setUser(user);
        this.projectRepository.save(project);
        this.projectMemberRepository.save(projectMember);
        return project;
    }

    public List<Project> findAll() {
       return this.projectRepository.findAll();
    }
}
