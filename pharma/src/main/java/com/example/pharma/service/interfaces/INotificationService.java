package com.example.pharma.service.interfaces;

public interface INotificationService {
    void sendEmailNotification(String email, String body, String subject);
}

