package com.mvt.backApp.user.service;

import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.common.id.BusinessIdKind;
import com.mvt.backApp.common.id.BusinessIdService;
import com.mvt.backApp.common.security.RoleNames;
import com.mvt.backApp.common.service.FileStorageService;
import com.mvt.backApp.patient.dto.PatientRegistrationDto;
import com.mvt.backApp.patient.entity.PatientProfile;
import com.mvt.backApp.patient.repository.PatientProfileRepository;
import com.mvt.backApp.security.service.JwtService;
import com.mvt.backApp.user.entity.Gender;
import com.mvt.backApp.user.entity.OtpPurpose;
import com.mvt.backApp.user.entity.Role;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.RoleRepository;
import com.mvt.backApp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final UUID USER_ID = UUID.fromString(
            "44444444-4444-4444-4444-444444444444"
    );

    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CarePartnerProfileRepository partnerRepository;
    @Mock
    private PatientProfileRepository patientRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailOtpService emailOtpService;
    @Mock
    private BusinessIdService businessIdService;

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
                jwtService,
                emailOtpService,
                businessIdService
        );
    }

    @Test
    void registersPatientUsingCurrentRegistrationModel() {
        PatientRegistrationDto request = patientRequest();
        Role patientRole = role(RoleNames.PATIENT);

        when(roleRepository.findByName(RoleNames.PATIENT))
                .thenReturn(Optional.of(patientRole));
        when(passwordEncoder.encode("StrongPass1!"))
                .thenReturn("encoded-password");
        when(userRepository.existsByEmailIgnoreCase(
                "patient@example.test"
        )).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(USER_ID);
                    return user;
                });
        when(businessIdService.next(BusinessIdKind.PATIENT, "IN"))
                .thenReturn("PATIN1001");
        when(patientRepository.save(any(PatientProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.registerPatient(request);

        assertThat(response.getPatientId()).isEqualTo("PATIN1001");
        assertThat(response.getEmail()).isEqualTo("patient@example.test");
        assertThat(response.getReferredByPartnerId()).isNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail())
                .isEqualTo("patient@example.test");
        assertThat(userCaptor.getValue().getGender())
                .isEqualTo(Gender.OTHER);
        assertThat(userCaptor.getValue().getPasswordHash())
                .isEqualTo("encoded-password");
        assertThat(userCaptor.getValue().isEmailVerified()).isTrue();

        ArgumentCaptor<PatientProfile> profileCaptor =
                ArgumentCaptor.forClass(PatientProfile.class);
        verify(patientRepository).save(profileCaptor.capture());

        PatientProfile profile = profileCaptor.getValue();
        assertThat(profile.getPatientId()).isEqualTo("PATIN1001");
        assertThat(profile.getCountryCode()).isEqualTo("IN");
        assertThat(profile.getMobileNumber()).isEqualTo("+919876543210");
        assertThat(profile.isWhatsappSameAsMobile()).isTrue();
        assertThat(profile.getWhatsappNumber()).isEqualTo("+919876543210");
        assertThat(profile.getReferredBy()).isNull();
        assertThat(profile.getReferredAt()).isNull();

        verify(emailOtpService).verifyAndConsume(
                "patient@example.test",
                OtpPurpose.PATIENT_REGISTRATION,
                "123456"
        );
    }

    @Test
    void storesSeparateWhatsappNumberWhenRequested() {
        PatientRegistrationDto request = patientRequest();
        request.setWhatsappSameAsMobile(false);
        request.setWhatsappNumber("+919111111111");

        when(roleRepository.findByName(RoleNames.PATIENT))
                .thenReturn(Optional.of(role(RoleNames.PATIENT)));
        when(userRepository.existsByEmailIgnoreCase(
                "patient@example.test"
        )).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(businessIdService.next(BusinessIdKind.PATIENT, "IN"))
                .thenReturn("PATIN1002");
        when(patientRepository.save(any(PatientProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.registerPatient(request);

        ArgumentCaptor<PatientProfile> captor =
                ArgumentCaptor.forClass(PatientProfile.class);
        verify(patientRepository).save(captor.capture());
        assertThat(captor.getValue().isWhatsappSameAsMobile()).isFalse();
        assertThat(captor.getValue().getWhatsappNumber())
                .isEqualTo("+919111111111");
    }

    @Test
    void rejectsDuplicatePatientEmailBeforeOtpConsumption() {
        PatientRegistrationDto request = patientRequest();

        when(userRepository.existsByEmailIgnoreCase(
                "patient@example.test"
        )).thenReturn(true);

        assertThatThrownBy(() -> service.registerPatient(request))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception -> assertThat(exception.getCode())
                                .isEqualTo("DUPLICATE_EMAIL")
                );

        verify(emailOtpService, never()).verifyAndConsume(
                any(),
                any(),
                any()
        );
        verify(userRepository, never()).save(any(User.class));
        verify(patientRepository, never()).save(any(PatientProfile.class));
    }

    @Test
    void rejectsPasswordMismatchWhenServiceIsCalledDirectly() {
        PatientRegistrationDto request = patientRequest();
        request.setConfirmPassword("DifferentPass1!");

        assertThatThrownBy(() -> service.registerPatient(request))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception -> {
                            assertThat(exception.getCode())
                                    .isEqualTo("PASSWORDS_DO_NOT_MATCH");
                            assertThat(exception.getMessageKey())
                                    .isEqualTo(
                                            "validation.confirmPassword.mismatch"
                                    );
                        }
                );

        verify(userRepository, never()).save(any(User.class));
        verify(patientRepository, never()).save(any(PatientProfile.class));
    }

    @Test
    void requiresWhatsappNumberWhenItDiffersFromMobile() {
        PatientRegistrationDto request = patientRequest();
        request.setWhatsappSameAsMobile(false);
        request.setWhatsappNumber(null);

        assertThatThrownBy(() -> service.registerPatient(request))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception -> assertThat(exception.getCode())
                                .isEqualTo("WHATSAPP_REQUIRED")
                );

        verify(userRepository, never()).save(any(User.class));
        verify(patientRepository, never()).save(any(PatientProfile.class));
    }

    private PatientRegistrationDto patientRequest() {
        PatientRegistrationDto request = new PatientRegistrationDto();
        request.setFirstName("Test");
        request.setLastName("Patient");
        request.setEmail("Patient@Example.Test");
        request.setGender(Gender.OTHER);
        request.setPassword("StrongPass1!");
        request.setConfirmPassword("StrongPass1!");
        request.setCountryCode("IN");
        request.setMobileNumber("+919876543210");
        request.setWhatsappSameAsMobile(true);
        request.setEmailOtp("123456");
        return request;
    }

    private Role role(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }
}
