package com.vlz.laborexchange_notificationservice.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class StoredNotificationDto {
    Long id;
    String type;
    String message;
    boolean isRead;
    LocalDateTime createdAt;
}
