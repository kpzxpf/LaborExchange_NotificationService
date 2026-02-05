package com.vlz.laborexchange_notificationservice.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.vlz.laborexchange_notificationservice.dto.NotificationEvent;
import com.vlz.laborexchange_notificationservice.dto.NotificationProperties;
import com.vlz.laborexchange_notificationservice.dto.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationProperties properties;

    @InjectMocks
    private NotificationService notificationService;

    private final String RECIPIENT = "test@example.com";
    private final NotificationType TYPE_CODE = NotificationType.NEW_APPLICATION;

    @Test
    @DisplayName("createEmail: успех — данные извлекаются из интерфейса")
    void createEmail_Success() {
        // Arrange
        NotificationEvent event = mock(NotificationEvent.class);
        when(event.getRecipientEmail()).thenReturn(RECIPIENT);
        when(event.getTypeCode()).thenReturn(TYPE_CODE);
        when(event.getBodyArgs()).thenReturn(new Object[]{"Java Developer"});

        NotificationProperties.Template template = new NotificationProperties.Template();
        template.setSubject("Subject");
        template.setBody("Body for %s");

        when(properties.getTemplates()).thenReturn(Map.of(TYPE_CODE, template));

        // Act
        notificationService.createEmail(event);

        // Assert
        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                msg.getTo()[0].equals(RECIPIENT) &&
                        msg.getText().equals("Body for Java Developer")
        ));
    }

    @Test
    @DisplayName("createEmail: шаблон не найден — логирование ошибки")
    void createEmail_TemplateNotFound() {
        // Arrange
        NotificationEvent event = mock(NotificationEvent.class);
        when(event.getTypeCode()).thenReturn(TYPE_CODE);
        when(properties.getTemplates()).thenReturn(Map.of());

        // Act
        notificationService.createEmail(event);

        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}