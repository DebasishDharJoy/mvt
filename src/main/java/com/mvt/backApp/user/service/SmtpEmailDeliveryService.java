package com.mvt.backApp.user.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailDeliveryService
        implements EmailDeliveryService {

    private final JavaMailSender sender;
    private final String fromAddress;

    public SmtpEmailDeliveryService(
            @Qualifier("otpMailSender")
            JavaMailSender sender,

            @Value("${app.mail.accounts.otp.from-address}")
            String fromAddress
    ) {
        this.sender = sender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendRegistrationOtp(
            String address,
            String otp
    ) {
        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(address);
        message.setSubject(
                "Healthcare MVT verification code"
        );

        message.setText(
                "Your verification code is "
                        + otp
                        + ". It expires in 10 minutes."
        );

        sender.send(message);
    }
}