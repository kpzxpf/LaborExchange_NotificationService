package com.vlz.laborexchange_notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_notificationservice.dto.event.RejectedApplicationEvent;
import com.vlz.laborexchange_notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RejectedApplicationNotificationListener {
    private final NotificationService notificationService;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "${spring.kafka.topics.rejected-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            RejectedApplicationEvent event = mapper.readValue(message, RejectedApplicationEvent.class);
            log.info("Event received: {}", event);

            notificationService.createEmail(event);
        } catch (Exception e) {
            log.error("JSON parsing error", e);
        }
    }
}
