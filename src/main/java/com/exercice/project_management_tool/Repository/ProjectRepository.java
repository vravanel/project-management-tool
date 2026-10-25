package com.exercice.project_management_tool.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.Project;

/**
 * Accès aux projets
 * ProjectRepository
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
}
