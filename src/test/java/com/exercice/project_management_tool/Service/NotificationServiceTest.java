package com.exercice.project_management_tool.Service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.exercice.project_management_tool.TestData;
import com.exercice.project_management_tool.DTO.NotificationResponse;
import com.exercice.project_management_tool.Entity.Notification;
import com.exercice.project_management_tool.Entity.Project;
import com.exercice.project_management_tool.Entity.Task;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.NotificationRepository;
import com.exercice.project_management_tool.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private final User admin = TestData.user(1L, "admin@test.com");
    private final User test = TestData.user(2L, "test@test.com");
    private final Project project = TestData.project(1L, admin);

    @Test
    void notifyAssignment_avecAssigne_enregistreLaNotificationEtEnvoieLeMail() {

        Task task = TestData.task(10L, project, test);

        notificationService.notifyAssignment(task);

        verify(notificationRepository).save(any(Notification.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void markAsRead_succes_marqueCommeLue() {
        Notification notification = new Notification();
        notification.setTask(TestData.task(10L, project, admin));
        notification.setUser(test);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse response = notificationService.markAsRead(1L, 2L);

        assertThat(response.isRead()).isTrue();
    }
}
