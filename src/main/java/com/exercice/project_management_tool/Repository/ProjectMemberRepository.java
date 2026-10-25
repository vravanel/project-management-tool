package com.exercice.project_management_tool.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.ProjectMember;
import com.exercice.project_management_tool.Entity.User;

/**
 * Accès des utilisateurs aux projets
 * ProjectMemberRepository
 */
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    /**
     * Membres d'un projet
     */
    List<ProjectMember> findByProject(Project project);
    /**
     * Appartenance d'un utilisateur à un projet
     */
    Optional<ProjectMember> findByProjectAndUser(Project project, User user);
    /**
     * Appartenant d'un utilisateur à des projets
     */
    List<ProjectMember> findByUser(User user);
}
