package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Service.ProjectService;

@Controller
@RequestMapping("/api/project")
public class ProjectController {
    
    public final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping()
    public List<Project> findAll() {
        return this.projectService.findAll();
    }

    @PostMapping("/{userId}")
    public Project create(Project project, @RequestParam Long userId) {
        return this.projectService.create(project, userId);
    }

}
