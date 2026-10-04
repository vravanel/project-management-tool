package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.ProjectCreateRequest;
import com.exercice.project_management_tool.DTO.ProjectResponse;
import com.exercice.project_management_tool.Service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    public final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping()
    public List<ProjectResponse> findByUser(@RequestParam Long userId) {
        return this.projectService.findByUser(userId);
    }

    @PostMapping("/{userId}")
    public ProjectResponse create(@PathVariable Long userId, @RequestBody  @Valid ProjectCreateRequest request) {
        return this.projectService.create(request, userId);
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Long id, @RequestParam Long userId) {
        return this.projectService.findById(id, userId);
    }

}
