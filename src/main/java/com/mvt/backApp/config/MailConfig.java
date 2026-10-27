package com.mvt.backApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(
            @Value("${spring.mail.host:localhost}")
            String host,

            @Value("${spring.mail.port:1025}")
            int port,

            @Value("${spring.mail.username:}")
            String username,

            @Value("${spring.mail.password:}")
            String password,

            @Value("${spring.mail.properties.mail.smtp.auth:false}")
            boolean authenticationEnabled,

            @Value(
                    "${spring.mail.properties.mail.smtp.starttls.enable:false}"
            )
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

        return sender;
    }
}
