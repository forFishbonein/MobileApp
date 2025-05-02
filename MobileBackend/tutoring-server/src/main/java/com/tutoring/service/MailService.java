package com.tutoring.service;

public interface MailService {

    void sendVerificationCode(String toEmail, String code);
    void sendResetLink(String toEmail, String subject, String text);
    void sendLessonNotification(String toEmail,
                                String courseName,
                                String lessonTitle);
}
