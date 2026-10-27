package com.mvt.backApp.user.service;
import lombok.RequiredArgsConstructor;import org.springframework.mail.SimpleMailMessage;import org.springframework.mail.javamail.JavaMailSender;import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor public class SmtpEmailDeliveryService implements EmailDeliveryService {
 private final JavaMailSender sender;public void sendRegistrationOtp(String address,String otp){SimpleMailMessage m=new SimpleMailMessage();m.setTo(address);m.setSubject("Healthcare MVT verification code");m.setText("Your verification code is "+otp+". It expires in 10 minutes.");sender.send(m);} }
