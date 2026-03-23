package com.vlz.laborexchange_notificationservice.dto;

public interface NotificationEvent {
    String getRecipientEmail();
    Long getRecipientUserId();
    NotificationType getTypeCode();
    Object[] getBodyArgs();
}