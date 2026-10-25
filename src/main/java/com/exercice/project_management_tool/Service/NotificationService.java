package com.exercice.project_management_tool.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.DTO.NotificationResponse;
import com.exercice.project_management_tool.Entity.Notification;
import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.NotificationRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

/**
 * Notifications d'assignation de la tâche
 * NotificationService
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
            JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    /**
     * Notifie l'utilisateur assigné à une tâche (notifie + email)
     * @param task la tâche qui vient d'être assignée
     */
    public void notifyAssignment(Task task) {
        User assignee = task.getUser();
        if (assignee == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setTask(task);
        notification.setUser(assignee);
        notification.setType("TASK_ASSIGNED");
        this.notificationRepository.save(notification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(assignee.getEmail());
        message.setSubject("Nouvelle tâche assignée : " + task.getName());
        message.setText("Bonjour " + assignee.getUsername() + ", \n\nLa tâche \"" + task.getName() + "\" vous a été assignée.");
       try {
            this.mailSender.send(message);
       } catch (MailException e) {
        System.err.println("Echec d'envoie du mail à " + assignee.getEmail() + " : " + e.getMessage());
       }
    }

    /**
     * Liste les notifications d'un utilisateur
     * @param userId identifiant d'un utilisateur
     */
    public List<NotificationResponse> findByUser(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return this.notificationRepository.getNotificationByUser(user).stream().map(NotificationResponse::from)
                .toList();
    }

    /**
     * Marque une notification comme lue
     * @param id identifiant de la notification
     * @param userId identifiant d'un utilisateur
     */
    public NotificationResponse markAsRead(Long id, Long userId) {
        Notification newNotification = this.notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!newNotification.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette notification ne vous appartient pas");
        }        
        newNotification.setIsRead(true);
        return NotificationResponse.from(this.notificationRepository.save(newNotification));
    }
}
