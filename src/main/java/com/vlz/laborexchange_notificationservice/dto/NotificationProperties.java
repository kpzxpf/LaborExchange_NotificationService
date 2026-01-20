package com.vlz.laborexchange_notificationservice.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "app.notifications")
public class NotificationProperties {
    private Map<NotificationType, Template> templates;

    @Data
    public static class Template {
        private String subject;
        private String body;
    }
}