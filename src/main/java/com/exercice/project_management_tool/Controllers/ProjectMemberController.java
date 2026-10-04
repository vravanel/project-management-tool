package com.exercice.project_management_tool.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.ProjectMemberInviteRequest;
import com.exercice.project_management_tool.DTO.ProjectMemberResponse;
import com.exercice.project_management_tool.DTO.ProjectMemberRoleUpdateRequest;
import com.exercice.project_management_tool.Service.ProjectMemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/project/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PostMapping()
    public ProjectMemberResponse invite(@PathVariable Long projectId,
            @RequestBody @Valid ProjectMemberInviteRequest request) {
        return this.projectMemberService.invite(projectId, request);
    }

    @GetMapping()
    public List<ProjectMemberResponse> findByProject(@PathVariable Long projectId) {
        return this.projectMemberService.findByProject(projectId);
    }

    @PutMapping("/{memberId}")
    public ProjectMemberResponse updateRole(@PathVariable Long projectId, @PathVariable Long memberId,
            @RequestBody @Valid ProjectMemberRoleUpdateRequest request) {
        return this.projectMemberService.updateRole(memberId, request);
    }
}
