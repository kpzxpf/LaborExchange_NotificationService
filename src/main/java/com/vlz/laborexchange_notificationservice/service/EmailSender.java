package com.vlz.laborexchange_notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    @Retryable(
            retryFor = {MailException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void send(String to, String subject, String body) {
        log.info("Sending email to {} (attempt in progress)", to);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailSender.send(mailMessage);
        log.info("Email successfully sent to {}", to);
    }

    @Recover
    public void recover(MailException e, String to, String subject, String body) {
        log.error("Failed to send email to {} (subject: '{}') after 3 attempts: {}", to, subject, e.getMessage());
    }
}
