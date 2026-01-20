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
public class RejectedApplicationEvent implements NotificationEvent {
    private Long applicationId;
    private String candidateEmail;
    private String vacancyTitle;

    @Override
    public String getRecipientEmail() {
        return candidateEmail;
    }

    @Override
    public NotificationType getTypeCode() {
        return NotificationType.REJECTED_APPLICATION;
    }

    @Override
    public Object[] getBodyArgs() {
        return new Object[]{vacancyTitle};
    }
}
