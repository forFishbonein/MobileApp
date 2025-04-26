package com.tutoring.service;

public interface MailService {

    void sendVerificationCode(String toEmail, String code);

    void sendResetLink(String toEmail, String resetLink);

    /** 课程新课通知 */
    void sendLessonNotification(String toEmail,
                                String courseName,
                                String lessonTitle);
}
