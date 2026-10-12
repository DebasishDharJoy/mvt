package com.mvt.backApp.user.service;

import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.common.security.RoleNames;
import com.mvt.backApp.common.service.FileStorageService;
import com.mvt.backApp.patient.dto.PatientRegistrationDto;
import com.mvt.backApp.patient.repository.PatientProfileRepository;
import com.mvt.backApp.security.service.JwtService;
import com.mvt.backApp.user.entity.Role;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.RoleRepository;
import com.mvt.backApp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final UUID USER_ID =
            UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Mock FileStorageService fileStorageService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RoleRepository roleRepository;
    @Mock UserRepository userRepository;
    @Mock CarePartnerProfileRepository partnerRepository;
    @Mock PatientProfileRepository patientRepository;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(
                fileStorageService,
                passwordEncoder,
                roleRepository,
                userRepository,
                partnerRepository,
                patientRepository,
                authenticationManager,
                jwtService
        );
    }

    @Test
    void registersIndianPatientWithNationalId() {
        PatientRegistrationDto request =
                patient("India", "1234-TEST", null);
        Role role = role(RoleNames.PATIENT);

        when(roleRepository.findByName(RoleNames.PATIENT))
                .thenReturn(Optional.of(role));
        when(passwordEncoder.encode("Test-password-1"))
                .thenReturn("encoded");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(USER_ID);
                    return user;
                });
        when(patientRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.registerPatient(request);

        assertThat(response.getPatientId()).startsWith("PAT-");
        assertThat(response.getEmail())
                .isEqualTo("patient@example.test");
    }

    @Test
    void registersInternationalPatientWithPassport() {
        PatientRegistrationDto request =
                patient("France", null, "TEST-PASSPORT-1");

        when(roleRepository.findByName(RoleNames.PATIENT))
                .thenReturn(Optional.of(role(RoleNames.PATIENT)));
        when(passwordEncoder.encode("Test-password-1"))
                .thenReturn("encoded");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(patientRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.registerPatient(request).getPatientId())
                .startsWith("PAT-");
    }

    @Test
    void rejectsDuplicatePatientEmail() {
        PatientRegistrationDto request =
                patient("India", "TEST-ID", null);

        when(userRepository.existsByEmail("patient@example.test"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.registerPatient(request))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo("DUPLICATE_EMAIL");
    }

    @Test
    void requiresNationalIdForIndianPatient() {
        PatientRegistrationDto request =
                patient("India", null, null);

        assertThatThrownBy(() -> service.registerPatient(request))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo("NATIONAL_ID_REQUIRED");
    }

    private PatientRegistrationDto patient(
            String country,
            String nationalId,
            String passport
    ) {
        PatientRegistrationDto request =
                new PatientRegistrationDto();
        request.setFirstName("Test");
        request.setLastName("Patient");
        request.setEmail("Patient@Example.Test");
        request.setPassword("Test-password-1");
        request.setCountry(country);
        request.setNationalId(nationalId);
        request.setPassportNumber(passport);
        return request;
    }

    private Role role(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }
}
