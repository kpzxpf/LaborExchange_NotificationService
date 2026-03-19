package com.vlz.laborexchange_notificationservice.service;

import com.vlz.laborexchange_notificationservice.dto.NotificationEvent;
import com.vlz.laborexchange_notificationservice.dto.NotificationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmailSender emailSender;
    private final NotificationProperties properties;

    public void createEmail(NotificationEvent event) {
        NotificationProperties.Template template = properties.getTemplates().get(event.getTypeCode());

        if (template == null) {
            log.error("Pattern not found for type: {}", event.getTypeCode());
            return;
        }

        String subject = template.getSubject();
        String body = String.format(template.getBody(), event.getBodyArgs());

        emailSender.send(event.getRecipientEmail(), subject, body);
    }
}