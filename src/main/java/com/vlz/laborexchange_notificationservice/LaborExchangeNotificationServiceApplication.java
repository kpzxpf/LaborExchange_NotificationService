package com.vlz.laborexchange_notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class LaborExchangeNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaborExchangeNotificationServiceApplication.class, args);
    }

}
