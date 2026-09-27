package com.exercice.project_management_tool.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exercice.project_management_tool.Entity.Notification;
import com.exercice.project_management_tool.Entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> getNotificationByUser(User user);
}
