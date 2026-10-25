package com.exercice.project_management_tool.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.User;

/**
 * Accès aux utilisateurs
 * UserRepository
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Retrouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);
}
