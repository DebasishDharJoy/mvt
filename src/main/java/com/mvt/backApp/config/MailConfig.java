package com.mvt.backApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    /*
     * Default sender.
     *
     * Existing code that injects JavaMailSender without a qualifier
     * will receive this bean because it is @Primary.
     */
    @Bean(name = {"javaMailSender", "otpMailSender"})
    @Primary
    public JavaMailSender otpMailSender(
            @Value("${app.mail.accounts.otp.host}")
            String host,

            @Value("${app.mail.accounts.otp.port:587}")
            int port,

            @Value("${app.mail.accounts.otp.username:}")
            String username,

            @Value("${app.mail.accounts.otp.password:}")
            String password,

            @Value("${app.mail.accounts.otp.smtp-auth:true}")
            boolean authenticationEnabled,

            @Value("${app.mail.accounts.otp.starttls:true}")
            boolean startTlsEnabled
    ) {
        return createMailSender(
                host,
                port,
                username,
                password,
                authenticationEnabled,
                startTlsEnabled
        );
    }


//    @Bean(name = "notificationMailSender")
//    public JavaMailSender notificationMailSender(
//            @Value("${app.mail.accounts.notification.host}")
//            String host,
//
//            @Value("${app.mail.accounts.notification.port:587}")
//            int port,
//
//            @Value("${app.mail.accounts.notification.username:}")
//            String username,
//
//            @Value("${app.mail.accounts.notification.password:}")
//            String password,
//
//            @Value("${app.mail.accounts.notification.smtp-auth:true}")
//            boolean authenticationEnabled,
//
//            @Value("${app.mail.accounts.notification.starttls:true}")
//            boolean startTlsEnabled
//    ) {
//        return createMailSender(
//                host,
//                port,
//                username,
//                password,
//                authenticationEnabled,
//                startTlsEnabled
//        );
//    }
//
//
//    @Bean(name = "supportMailSender")
//    public JavaMailSender supportMailSender(
//            @Value("${app.mail.accounts.support.host}")
//            String host,
//
//            @Value("${app.mail.accounts.support.port:587}")
//            int port,
//
//            @Value("${app.mail.accounts.support.username:}")
//            String username,
//
//            @Value("${app.mail.accounts.support.password:}")
//            String password,
//
//            @Value("${app.mail.accounts.support.smtp-auth:true}")
//            boolean authenticationEnabled,
//
//            @Value("${app.mail.accounts.support.starttls:true}")
//            boolean startTlsEnabled
//    ) {
//        return createMailSender(
//                host,
//                port,
//                username,
//                password,
//                authenticationEnabled,
//                startTlsEnabled
//        );
//    }


    private JavaMailSender createMailSender(
            String host,
            int port,
            String username,
            String password,
            boolean authenticationEnabled,
            boolean startTlsEnabled
    ) {
        JavaMailSenderImpl sender =
                new JavaMailSenderImpl();

        sender.setHost(host);
        sender.setPort(port);
        sender.setDefaultEncoding("UTF-8");

        if (username != null && !username.isBlank()) {
            sender.setUsername(username);
        }

        if (password != null && !password.isBlank()) {
            sender.setPassword(password);
        }

        Properties properties =
                sender.getJavaMailProperties();

        properties.put(
                "mail.smtp.auth",
                Boolean.toString(authenticationEnabled)
        );

        properties.put(
                "mail.smtp.starttls.enable",
                Boolean.toString(startTlsEnabled)
        );

        properties.put(
                "mail.smtp.connectiontimeout",
                "10000"
        );

        properties.put(
                "mail.smtp.timeout",
                "10000"
        );

        properties.put(
                "mail.smtp.writetimeout",
                "10000"
        );

        return sender;
    }
}