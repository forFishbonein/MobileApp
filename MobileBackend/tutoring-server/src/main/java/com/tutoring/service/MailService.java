package com.tutoring.service;

public interface MailService {

    void sendVerificationCode(String toEmail, String code);
    /**
     * 通用邮件发送
     * @param toEmail 收件人
     * @param subject 邮件主题
     * @param text    邮件正文（纯文本）
     */
    void sendResetLink(String toEmail, String subject, String text);
    /** 课程新课通知 */
    void sendLessonNotification(String toEmail,
                                String courseName,
                                String lessonTitle);
}
