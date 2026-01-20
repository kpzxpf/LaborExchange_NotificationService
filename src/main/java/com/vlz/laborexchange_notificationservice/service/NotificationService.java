package com.vlz.laborexchange_notificationservice.service;

import com.vlz.laborexchange_notificationservice.dto.NotificationEvent;
import com.vlz.laborexchange_notificationservice.dto.NotificationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final NotificationProperties properties;

    public void createEmail(NotificationEvent event) {
        NotificationProperties.Template template = properties.getTemplates().get(event.getTypeCode());

        if (template == null) {
            log.error("Pattern not found for type: {}", event.getTypeCode());
            return;
        }

        try {
            String subject = template.getSubject();
            String body = String.format(template.getBody(), event.getBodyArgs());

            sendMail(event.getRecipientEmail(), subject, body);

            log.info("Email successfully sent to {} for event {}",
                    event.getRecipientEmail(), event.getTypeCode());
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", event.getTypeCode(), e.getMessage());
        }
    }

    private void sendMail(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        mailSender.send(mailMessage);
    }
}