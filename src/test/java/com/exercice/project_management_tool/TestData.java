package com.exercice.project_management_tool;

import java.time.LocalDate;

import org.springframework.test.util.ReflectionTestUtils;

import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.User;

public class TestData {
    
    public static final String TASK_NAME = "Ma tâche";
    public static final String TASK_DESCRIPTION = "Description de la tâche";
    public static final LocalDate TASK_DUE_DATE = LocalDate.of(2026,11,1);

    private TestData() {
    }

    public static User user(Long id, String email) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        user.setUsername("user" + id);
        user.setEmail(email);
        return user;
    }

    public static ProjectMember member(Long id, User user, Project project, ProjectMember.Role role) {
        ProjectMember member = new ProjectMember();
        ReflectionTestUtils.setField(member, "id", id);
        member.setUser(user);
        member.setProject(project);
        member.setRole(role);
        return member;
    }

    public static Task task(Long id, Project project, User assignee)  {
        Task task = new Task();
        ReflectionTestUtils.setField(task, "id", id);
        task.setName(TASK_NAME);
        task.setDescription(TASK_DESCRIPTION);
        task.setPriority(Task.Priority.MOYENNE);
        task.setStatus(Task.Status.A_FAIRE);
        task.setDueDate(TASK_DUE_DATE);
        task.setProject(project);
        task.setUser(assignee);
        return task;
    }

    public static Project project(Long id, User creator) {
        Project project = new Project();
        ReflectionTestUtils.setField(project, "id", id);
        project.setTitle("Projet de test");
        project.setDescription("Description du projet");
        project.setStartDate(LocalDate.of(2026,10,1));
        project.setCreator(creator);
        return project;
    }
}
