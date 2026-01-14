package com.vlz.laborexchange_notificationservice.service;

import com.vlz.laborexchange_notificationservice.dto.ApplicationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    public void sendEmail(ApplicationEvent event) {
        log.info("Sending Email to: {} | Subject: {} | Body: {}");
        //TODO: Сделать создание сообщения
    }
}
