package com.mvt.backApp.user.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpNotificationEmailService
        implements NotificationEmailService {

    private final JavaMailSender sender;
    private final String fromAddress;

    public SmtpNotificationEmailService(
            @Qualifier("notificationMailSender")
            JavaMailSender sender,

            @Value("${app.mail.accounts.notification.from-address}")
            String fromAddress
    ) {
        this.sender = sender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendNotification(
            String recipient,
            String subject,
            String body
    ) {
        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);

        sender.send(message);
    }
}
