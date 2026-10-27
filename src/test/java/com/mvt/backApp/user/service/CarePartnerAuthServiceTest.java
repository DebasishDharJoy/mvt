package com.mvt.backApp.user.service;

import com.mvt.backApp.agent.dto.CarePartnerRegistrationDto;
import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.agent.entity.IdentityDocumentType;
import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.dto.LoginRequestDto;
import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.common.id.BusinessIdKind;
import com.mvt.backApp.common.id.BusinessIdService;
import com.mvt.backApp.common.security.RoleNames;
import com.mvt.backApp.common.service.FileStorageService;
import com.mvt.backApp.patient.repository.PatientProfileRepository;
import com.mvt.backApp.security.model.AuthenticatedUser;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarePartnerAuthServiceTest {

    private static final UUID USER_ID = UUID.fromString(
            "33333333-3333-3333-3333-333333333333"
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
    void registersCarePartnerUsingCurrentRegistrationModel() throws Exception {
        CarePartnerRegistrationDto request = registrationRequest(
                "application/pdf"
        );
        prepareRegistration("IN", "AGTIN1001");

        ArgumentCaptor<CarePartnerProfile> profileCaptor =
                ArgumentCaptor.forClass(CarePartnerProfile.class);
        when(partnerRepository.save(profileCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.registerCarePartner(request);

        assertThat(response.getApprovalStatus())
                .isEqualTo(ApprovalStatus.PENDING);
        assertThat(response.getPartnerId()).isEqualTo("AGTIN1001");
        assertThat(response.getEmail()).isEqualTo("partner@example.test");

        CarePartnerProfile profile = profileCaptor.getValue();
        assertThat(profile.getPartnerId()).isEqualTo("AGTIN1001");
        assertThat(profile.getCompanyName()).isEqualTo("Test Partner Company");
        assertThat(profile.getCountryCode()).isEqualTo("IN");
        assertThat(profile.getMobileNumber()).isEqualTo("+919876543210");
        assertThat(profile.isWhatsappSameAsMobile()).isTrue();
        assertThat(profile.getWhatsappNumber()).isEqualTo("+919876543210");
        assertThat(profile.getDocumentType())
                .isEqualTo(IdentityDocumentType.TRADE_LICENSE);
        assertThat(profile.getDocumentNumber()).isEqualTo("TRADE-TEST-001");
        assertThat(profile.getVerificationDocumentUrl())
                .isEqualTo("/uploads/documents/generated.pdf");
        assertThat(profile.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);

        verify(emailOtpService).verifyAndConsume(
                "partner@example.test",
                OtpPurpose.CARE_PARTNER_REGISTRATION,
                "123456"
        );
        verify(businessIdService).next(BusinessIdKind.AGENT, "IN");
    }

    @Test
    void storesSeparateWhatsappNumberForCarePartner() throws Exception {
        CarePartnerRegistrationDto request = registrationRequest(
                "image/jpeg"
        );
        request.setCountryCode("FR");
        request.setWhatsappSameAsMobile(false);
        request.setWhatsappNumber("+33123456789");
        request.setDocumentType(IdentityDocumentType.PASSPORT);
        request.setDocumentNumber("PASSPORT-TEST-001");

        prepareRegistration("FR", "AGTFR1001");

        ArgumentCaptor<CarePartnerProfile> profileCaptor =
                ArgumentCaptor.forClass(CarePartnerProfile.class);
        when(partnerRepository.save(profileCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.registerCarePartner(request);

        CarePartnerProfile profile = profileCaptor.getValue();
        assertThat(profile.getCountryCode()).isEqualTo("FR");
        assertThat(profile.isWhatsappSameAsMobile()).isFalse();
        assertThat(profile.getWhatsappNumber()).isEqualTo("+33123456789");
        assertThat(profile.getDocumentType())
                .isEqualTo(IdentityDocumentType.PASSPORT);
        assertThat(profile.getDocumentNumber())
                .isEqualTo("PASSPORT-TEST-001");
    }

    @Test
    void rejectsUnsupportedDocumentType() throws Exception {
        CarePartnerRegistrationDto request = registrationRequest("image/png");

        assertThatThrownBy(() -> service.registerCarePartner(request))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception -> assertThat(exception.getCode())
                                .isEqualTo("INVALID_DOCUMENT_TYPE")
                );

        verify(fileStorageService, never()).uploadFile(any(), any());
        verify(userRepository, never()).save(any(User.class));
        verify(partnerRepository, never()).save(any(CarePartnerProfile.class));
    }

    @Test
    void rejectsDocumentLargerThanTenMegabytes() throws Exception {
        CarePartnerRegistrationDto request = registrationRequest(
                "application/pdf"
        );
        request.setVerificationDocument(new MockMultipartFile(
                "verificationDocument",
                "large.pdf",
                "application/pdf",
                new byte[(10 * 1024 * 1024) + 1]
        ));

        assertThatThrownBy(() -> service.registerCarePartner(request))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception -> assertThat(exception.getCode())
                                .isEqualTo("DOCUMENT_TOO_LARGE")
                );

        verify(fileStorageService, never()).uploadFile(any(), any());
    }

    @Test
    void rejectsDuplicateCarePartnerEmailBeforeOtpConsumption() {
        CarePartnerRegistrationDto request = registrationRequest(
                "application/pdf"
        );
        when(userRepository.existsByEmailIgnoreCase(
                "partner@example.test"
        )).thenReturn(true);

        assertThatThrownBy(() -> service.registerCarePartner(request))
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
        verify(partnerRepository, never()).save(any(CarePartnerProfile.class));
    }

    @Test
    void pendingCarePartnerCannotLogin() {
        assertLoginDenied(
                ApprovalStatus.PENDING,
                "CARE_PARTNER_PENDING"
        );
    }

    @Test
    void rejectedCarePartnerCannotLogin() {
        assertLoginDenied(
                ApprovalStatus.REJECTED,
                "CARE_PARTNER_REJECTED"
        );
    }

    @Test
    void approvedCarePartnerReceivesStandardJwtResponse() {
        User user = carePartnerUser();
        CarePartnerProfile profile = profile(
                user,
                ApprovalStatus.APPROVED
        );
        AuthenticatedUser principal = principal();

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        ));
        when(userRepository.findByEmail("partner@example.test"))
                .thenReturn(Optional.of(user));
        when(partnerRepository.findByUser(user))
                .thenReturn(Optional.of(profile));
        when(jwtService.generateToken(principal))
                .thenReturn("test-token-not-logged");

        var response = service.login(loginRequest());

        assertThat(response.getToken()).isEqualTo("test-token-not-logged");
        assertThat(response.getUserId()).isEqualTo(USER_ID);
        assertThat(response.getEmail()).isEqualTo("partner@example.test");
        assertThat(response.getRole()).isEqualTo(RoleNames.CARE_PARTNER);
        assertThat(response.getPermissions()).isEmpty();
    }

    private void assertLoginDenied(
            ApprovalStatus status,
            String expectedCode
    ) {
        User user = carePartnerUser();
        CarePartnerProfile profile = profile(user, status);
        AuthenticatedUser principal = principal();

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        ));
        when(userRepository.findByEmail("partner@example.test"))
                .thenReturn(Optional.of(user));
        when(partnerRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> service.login(loginRequest()))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception -> assertThat(exception.getCode())
                                .isEqualTo(expectedCode)
                );
    }

    private void prepareRegistration(
            String countryCode,
            String generatedPartnerId
    ) throws Exception {
        when(roleRepository.findByName(RoleNames.CARE_PARTNER))
                .thenReturn(Optional.of(carePartnerRole()));
        when(passwordEncoder.encode("StrongPass1!"))
                .thenReturn("encoded-password");
        when(userRepository.existsByEmailIgnoreCase(
                "partner@example.test"
        )).thenReturn(false);
        when(fileStorageService.uploadFile(any(), any()))
                .thenReturn("/uploads/documents/generated.pdf");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(USER_ID);
                    return user;
                });
        when(businessIdService.next(
                BusinessIdKind.AGENT,
                countryCode
        )).thenReturn(generatedPartnerId);
    }

    private CarePartnerRegistrationDto registrationRequest(
            String contentType
    ) {
        CarePartnerRegistrationDto request =
                new CarePartnerRegistrationDto();
        request.setFirstName("Test");
        request.setLastName("Partner");
        request.setEmail("Partner@Example.Test");
        request.setGender(Gender.OTHER);
        request.setPassword("StrongPass1!");
        request.setConfirmPassword("StrongPass1!");
        request.setCompanyName("Test Partner Company");
        request.setCountryCode("IN");
        request.setMobileNumber("+919876543210");
        request.setWhatsappSameAsMobile(true);
        request.setDocumentType(IdentityDocumentType.TRADE_LICENSE);
        request.setDocumentNumber("TRADE-TEST-001");
        request.setEmailOtp("123456");
        request.setVerificationDocument(new MockMultipartFile(
                "verificationDocument",
                "proof.pdf",
                contentType,
                new byte[]{1, 2, 3}
        ));
        return request;
    }

    private LoginRequestDto loginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("partner@example.test");
        request.setPassword("StrongPass1!");
        return request;
    }

    private AuthenticatedUser principal() {
        return new AuthenticatedUser(
                USER_ID,
                "partner@example.test",
                "encoded-password",
                RoleNames.CARE_PARTNER,
                List.of(),
                true
        );
    }

    private User carePartnerUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail("partner@example.test");
        user.setRole(carePartnerRole());
        return user;
    }

    private Role carePartnerRole() {
        Role role = new Role();
        role.setName(RoleNames.CARE_PARTNER);
        return role;
    }

    private CarePartnerProfile profile(
            User user,
            ApprovalStatus status
    ) {
        CarePartnerProfile profile = new CarePartnerProfile();
        profile.setUser(user);
        profile.setApprovalStatus(status);
        return profile;
    }
}
