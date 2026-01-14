package com.vlz.laborexchange_notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_notificationservice.dto.ApplicationEvent;
import com.vlz.laborexchange_notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationListener {
    private final NotificationService notificationService;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "${spring.kafka.topics.application-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            ApplicationEvent event = mapper.readValue(message, ApplicationEvent.class);
            log.info("Event received: {}", event);

            notificationService.sendEmail(event);
        } catch (Exception e) {
            log.error("JSON parsing error", e);
        }
    }
}