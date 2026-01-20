package com.vlz.laborexchange_notificationservice.dto.event;

import com.vlz.laborexchange_notificationservice.dto.NotificationEvent;
import com.vlz.laborexchange_notificationservice.dto.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewApplicationEvent implements NotificationEvent {
    private Long applicationId;
    private String employerEmail;
    private String vacancyTitle;

    @Override
    public String getRecipientEmail() {
        return employerEmail;
    }

    @Override
    public NotificationType getTypeCode() {
        return NotificationType.NEW_APPLICATION;
    }

    @Override
    public Object[] getBodyArgs() {
        return new Object[]{vacancyTitle};
    }
}
