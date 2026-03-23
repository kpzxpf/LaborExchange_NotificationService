package com.vlz.laborexchange_notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_notificationservice.dto.event.NewApplicationEvent;
import com.vlz.laborexchange_notificationservice.service.IdempotencyService;
import com.vlz.laborexchange_notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewApplicationNotificationListener {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "${spring.kafka.topics.new-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            NewApplicationEvent event = objectMapper.readValue(message, NewApplicationEvent.class);
            String eventId = "new-app-" + event.getApplicationId();
            if (!idempotencyService.tryMarkProcessed(eventId)) return;
            log.info("Event received: {}", event);
            notificationService.notify(event);
        } catch (Exception e) {
            log.error("Failed to process new application notification. Raw message: {}", message, e);
            throw new RuntimeException("Notification processing failed, routing to DLT", e);
        }
    }
}