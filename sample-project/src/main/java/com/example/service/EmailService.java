package com.example.service;

public interface EmailService {
    void sendWelcomeEmail(String email);
    void sendNotificationEmail(String email, String message);
} 