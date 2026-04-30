package com.example.pharma.service;

import com.example.pharma.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final EmailService emailService;

    @Override
    public void sendEmailNotification(String email, String body, String subject) {
        log.info("Sending email to {}: {}", email, subject);
        emailService.sendEmail(email, body, subject);
    }
}
