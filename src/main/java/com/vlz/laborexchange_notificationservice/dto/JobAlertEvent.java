package com.vlz.laborexchange_notificationservice.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAlertEvent {
    private Long subscriptionId;
    private Long userId;
    private String userEmail;
    private Long vacancyId;
    private String vacancyTitle;
    private String companyName;
    private String location;
    private Double salary;
    private String employmentType;
    private String workFormat;
    private Set<String> skills;
    private LocalDateTime publishedAt;
}
