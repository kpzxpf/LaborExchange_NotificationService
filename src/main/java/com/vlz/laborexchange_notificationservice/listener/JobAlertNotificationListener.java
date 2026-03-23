package com.vlz.laborexchange_notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_notificationservice.dto.JobAlertEvent;
import com.vlz.laborexchange_notificationservice.service.IdempotencyService;
import com.vlz.laborexchange_notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobAlertNotificationListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = "${spring.kafka.topics.job-alert}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message) {
        try {
            JobAlertEvent event = objectMapper.readValue(message, JobAlertEvent.class);
            String eventId = "job-alert-" + event.getSubscriptionId() + "-" + event.getVacancyId();
            if (!idempotencyService.tryMarkProcessed(eventId)) {
                log.debug("Duplicate job alert skipped: {}", eventId);
                return;
            }
            notificationService.notifyJobAlert(event);
        } catch (Exception e) {
            log.error("Error processing job alert event", e);
            throw new RuntimeException(e); // routes to DLT
        }
    }
}
