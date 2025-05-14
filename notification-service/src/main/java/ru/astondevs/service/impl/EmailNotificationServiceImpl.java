package ru.astondevs.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.astondevs.exception.EmailSendingException;
import ru.astondevs.service.EmailNotificationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${email.sender}")
    private String senderEmail;

    @Value("${email.reply-to}")
    private String replyToEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setReplyTo(replyToEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(mimeMessage);
            log.info("Email successfully sent to {} with subject '{}'", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to '{}' with subject '{}'. Error: {}", to, subject, e.getMessage());
            throw new EmailSendingException("Ошибка отправки email на " + to, e);
        }
    }
}