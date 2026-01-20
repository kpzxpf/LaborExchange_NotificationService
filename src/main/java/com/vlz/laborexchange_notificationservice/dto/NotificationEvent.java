package com.vlz.laborexchange_notificationservice.dto;

public interface NotificationEvent {
    String getRecipientEmail();
    NotificationType getTypeCode();
    Object[] getBodyArgs();
}