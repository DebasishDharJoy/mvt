package com.mvt.backApp.user.service;

public interface NotificationEmailService {

    void sendNotification(
            String recipient,
            String subject,
            String body
    );
}
