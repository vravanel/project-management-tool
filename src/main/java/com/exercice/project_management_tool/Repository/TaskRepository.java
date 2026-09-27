package com.exercice.project_management_tool.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Task.Status status);

}