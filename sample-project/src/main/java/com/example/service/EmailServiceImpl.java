package com.example.service;

public class EmailServiceImpl implements EmailService {
    
    @Override
    public void sendWelcomeEmail(String email) {
        System.out.println("Sending welcome email to: " + email);
    }
    
    @Override
    public void sendNotificationEmail(String email, String message) {
        System.out.println("Sending notification email to: " + email + " with message: " + message);
    }
} 