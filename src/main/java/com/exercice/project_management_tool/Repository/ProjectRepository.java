package com.exercice.project_management_tool.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    
}
