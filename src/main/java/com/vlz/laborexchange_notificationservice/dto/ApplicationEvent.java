package com.vlz.laborexchange_notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEvent {
    private Long applicationId;
    private Long employerId;
    private Long candidateId;
    private String vacancyTitle;
    private String statusCode;
}