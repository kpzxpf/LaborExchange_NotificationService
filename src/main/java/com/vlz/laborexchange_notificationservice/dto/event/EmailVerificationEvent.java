package com.vlz.laborexchange_notificationservice.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailVerificationEvent {
    private Long userId;
    private String email;
    private String token;
}
