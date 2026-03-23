package com.vlz.laborexchange_notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_notificationservice.dto.event.EmailVerificationEvent;
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
public class EmailVerificationListener {

    private final EmailSender emailSender;
    private final ObjectMapper objectMapper;
    private final IdempotencyService idempotencyService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @KafkaListener(topics = "${spring.kafka.topics.email-verification}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            EmailVerificationEvent event = objectMapper.readValue(message, EmailVerificationEvent.class);

            String eventId = "email-verify-" + event.getUserId() + "-" + event.getToken();
            if (!idempotencyService.tryMarkProcessed(eventId)) return;

            log.info("Email verification event received for userId={}", event.getUserId());

            String link = frontendUrl + "/auth/verify-email?token=" + event.getToken();
            String subject = "Подтвердите ваш email";
            String body = "Для подтверждения адреса электронной почты перейдите по ссылке:\n\n" + link
                    + "\n\nСсылка действительна 24 часа.";

            emailSender.send(event.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed to process email verification event. Raw message: {}", message, e);
            throw new RuntimeException("Email verification processing failed, routing to DLT", e);
        }
    }
}
