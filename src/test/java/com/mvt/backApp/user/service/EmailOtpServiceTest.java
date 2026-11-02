package com.mvt.backApp.user.service;

import com.mvt.backApp.user.entity.*;
import com.mvt.backApp.user.repository.EmailVerificationChallengeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailOtpServiceTest {
    @Mock
    EmailVerificationChallengeRepository repository;
    @Mock
    EmailDeliveryService delivery;
    @Mock
    PasswordEncoder encoder;
    @InjectMocks
    EmailOtpService service;

    @Test
    void consumesValidOtp() {
        EmailVerificationChallenge c = new EmailVerificationChallenge();
        c.setOtpHash("hash");
        c.setExpiresAt(LocalDateTime.now().plusMinutes(1));
        when(repository.findTopByEmailAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc("a@example.test", OtpPurpose.PATIENT_REGISTRATION)).thenReturn(Optional.of(c));
        when(encoder.matches("123456", "hash")).thenReturn(true);
        service.verifyAndConsume("A@EXAMPLE.TEST", OtpPurpose.PATIENT_REGISTRATION, "123456");
        assertThat(c.getConsumedAt()).isNotNull();
        verify(repository).save(c);
    }
}
