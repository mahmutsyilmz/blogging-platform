package com.web.blog.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationCode);
    void sendRequestNotification(String toEmail, String subject, String messageText);
}
