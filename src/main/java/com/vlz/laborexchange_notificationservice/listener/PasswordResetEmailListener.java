package com.vlz.laborexchange_notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_notificationservice.dto.event.PasswordResetEmailEvent;
import com.vlz.laborexchange_notificationservice.service.EmailSender;
import com.vlz.laborexchange_notificationservice.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetEmailListener {

    private final EmailSender emailSender;
    private final ObjectMapper objectMapper;
    private final IdempotencyService idempotencyService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @KafkaListener(topics = "${spring.kafka.topics.password-reset}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            PasswordResetEmailEvent event = objectMapper.readValue(message, PasswordResetEmailEvent.class);

            String eventId = "pwd-reset-" + event.getUserId() + "-" + event.getToken();
            if (!idempotencyService.tryMarkProcessed(eventId)) return;

            log.info("Password reset event received for userId={}", event.getUserId());

            String link = frontendUrl + "/auth/reset-password?token=" + event.getToken();
            String subject = "Сброс пароля";
            String body = "Для сброса пароля перейдите по ссылке:\n\n" + link
                    + "\n\nСсылка действительна 1 час. Если вы не запрашивали сброс пароля — проигнорируйте это письмо.";

            emailSender.send(event.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed to process password reset email event. Raw message: {}", message, e);
            throw new RuntimeException("Password reset email processing failed, routing to DLT", e);
        }
    }
}
