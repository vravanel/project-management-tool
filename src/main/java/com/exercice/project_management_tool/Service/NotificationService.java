package com.exercice.project_management_tool.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.Entity.Notification;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.NotificationRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> findByUser(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return this.notificationRepository.getNotificationByUser(user);
    }

    public Notification create(Long userId, Notification notification) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        notification.setUser(user);
        return this.notificationRepository.save(notification);
    }

    public Notification markAsRead(Long id) {
        Notification newNotification = this.notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        newNotification.setIsRead(true);
        return this.notificationRepository.save(newNotification);
    }
}
