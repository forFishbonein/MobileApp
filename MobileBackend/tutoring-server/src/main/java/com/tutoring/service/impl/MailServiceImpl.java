package com.tutoring.service.impl;

import com.tutoring.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async("mailAsyncExecutor")
    @Override
    public void sendVerificationCode(String toEmail, String code) {
        String subject = "Fitness App - Verification Code";
        String text = "Hello,\n\nYour verification code is: " + code + "\n\nPlease complete the verification within 5 minutes.";
        sendMail(toEmail, subject, text);
    }

    private void sendMail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("yourgmailEmail@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            mailSender.send(message);
            log.info("Email sent successfully: to={}, subject={}", to, subject);

        } catch (MessagingException e) {
            log.error("Failed to send email: to={}, subject={}, error={}", to, subject, e.getMessage());
        }
    }



    @Async("mailAsyncExecutor")
    @Override
    public void sendResetLink(String toEmail, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom("no-reply@yourapp.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, false);
            mailSender.send(message);
            log.info("Email sent: to={} subject={}", toEmail, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {} subject={}, error={}",
                    toEmail, subject, e.getMessage());
        }
    }

    @Async("mailAsyncExecutor")
    @Override
    public void sendLessonNotification(String toEmail,
                                       String courseName,
                                       String lessonTitle) {

        String subject = "New lesson released in \"" + courseName + '"';
        String text = "Hi there,\n\n" +
                "A new lesson \"" + lessonTitle + "\" has been published in course \"" +
                courseName + "\". Log in to check it out.\n\n" +
                "Best regards,\nTutoring Platform";
        sendMail(toEmail, subject, text);
    }
}