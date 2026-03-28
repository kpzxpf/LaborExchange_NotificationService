package com.vlz.laborexchange_notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_notificationservice.dto.JobAlertEvent;
import com.vlz.laborexchange_notificationservice.dto.NotificationEvent;
import com.vlz.laborexchange_notificationservice.dto.NotificationProperties;
import com.vlz.laborexchange_notificationservice.entity.Notification;
import com.vlz.laborexchange_notificationservice.repository.NotificationRepository;
import com.vlz.laborexchange_notificationservice.sse.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailSender emailSender;
    private final NotificationProperties properties;
    private final SseEmitterRegistry sseEmitterRegistry;
    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;

    public void notify(NotificationEvent event) {
        pushSse(event);
        persist(event);
        createEmail(event);
    }

    private void persist(NotificationEvent event) {
        if (event.getRecipientUserId() == null) return;
        NotificationProperties.Template template = properties.getTemplates().get(event.getTypeCode());
        String message = template != null
                ? formatSafe(template.getBody(), event.getBodyArgs())
                : event.getTypeCode().name();
        try {
            notificationRepository.save(Notification.builder()
                    .userId(event.getRecipientUserId())
                    .type(event.getTypeCode())
                    .message(message)
                    .build());
        } catch (Exception e) {
            log.error("Failed to persist notification for userId={}: {}", event.getRecipientUserId(), e.getMessage());
        }
    }

    public void createEmail(NotificationEvent event) {
        NotificationProperties.Template template = properties.getTemplates().get(event.getTypeCode());

        if (template == null) {
            log.error("No template found for notification type: {}", event.getTypeCode());
            return;
        }

        String subject = template.getSubject();
        String body;
        try {
            body = String.format(template.getBody(), event.getBodyArgs());
        } catch (Exception e) {
            log.error("Failed to format email body for type {}: {}", event.getTypeCode(), e.getMessage());
            body = template.getBody();
        }

        emailSender.send(event.getRecipientEmail(), subject, body);
    }

    private void pushSse(NotificationEvent event) {
        Long userId = event.getRecipientUserId();
        if (userId == null) return;

        NotificationProperties.Template template = properties.getTemplates().get(event.getTypeCode());
        String message = template != null
                ? formatSafe(template.getBody(), event.getBodyArgs())
                : event.getTypeCode().name();

        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "type", event.getTypeCode().name(),
                    "message", message
            ));
            sseEmitterRegistry.push(userId, json);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize SSE payload for userId={}: {}", userId, e.getMessage());
        }
    }

    public void notifyJobAlert(JobAlertEvent event) {
        try {
            String salaryText = event.getSalary() != null
                    ? String.format("%.0f ₽", event.getSalary())
                    : "Не указана";
            String skillsText = (event.getSkills() != null && !event.getSkills().isEmpty())
                    ? String.join(", ", event.getSkills())
                    : "Не указаны";

            String subject = "🔔 Новая вакансия по вашей подписке: " + event.getVacancyTitle();
            String body = String.format(
                    "Здравствуйте!\n\n" +
                    "По вашей подписке на вакансии появилась новая позиция:\n\n" +
                    "📌 Вакансия: %s\n" +
                    "🏢 Компания: %s\n" +
                    "📍 Местоположение: %s\n" +
                    "💰 Зарплата: %s\n" +
                    "⏰ Тип занятости: %s\n" +
                    "🏠 Формат работы: %s\n" +
                    "🛠️ Навыки: %s\n\n" +
                    "Перейдите на платформу, чтобы узнать подробности и откликнуться!\n\n" +
                    "С уважением,\nLaborExchange",
                    event.getVacancyTitle(),
                    event.getCompanyName() != null ? event.getCompanyName() : "Не указана",
                    event.getLocation() != null ? event.getLocation() : "Не указано",
                    salaryText,
                    event.getEmploymentType() != null ? event.getEmploymentType() : "Не указан",
                    event.getWorkFormat() != null ? event.getWorkFormat() : "Не указан",
                    skillsText
            );

            emailSender.send(event.getUserEmail(), subject, body);

            log.info("Job alert email sent to {} for vacancyId={}", event.getUserEmail(), event.getVacancyId());
        } catch (Exception e) {
            log.error("Failed to send job alert email to {}: {}", event.getUserEmail(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String formatSafe(String template, Object[] args) {
        try {
            return String.format(template, args);
        } catch (Exception e) {
            return template;
        }
    }
}
