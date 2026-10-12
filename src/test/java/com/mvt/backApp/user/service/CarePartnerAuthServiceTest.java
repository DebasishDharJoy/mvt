package com.mvt.backApp.user.service;

import com.mvt.backApp.agent.dto.CarePartnerRegistrationDto;
import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.dto.LoginRequestDto;
import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.common.security.RoleNames;
import com.mvt.backApp.common.service.FileStorageService;
import com.mvt.backApp.patient.repository.PatientProfileRepository;
import com.mvt.backApp.security.model.AuthenticatedUser;
import com.mvt.backApp.security.service.JwtService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarePartnerAuthServiceTest {

    private static final UUID USER_ID =
            UUID.fromString("33333333-3333-3333-3333-333333333333");

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
    void registersIndianCarePartnerAsPending() throws Exception {
        CarePartnerRegistrationDto request =
                request("India", "GST-TEST", null, "application/pdf");
        prepareRegistration();

        ArgumentCaptor<CarePartnerProfile> captor =
                ArgumentCaptor.forClass(CarePartnerProfile.class);
        when(partnerRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.registerCarePartner(request);

        assertThat(response.getApprovalStatus())
                .isEqualTo(ApprovalStatus.PENDING);
        assertThat(response.getPartnerId()).startsWith("AGT-");
        assertThat(captor.getValue().getGstNumber())
                .isEqualTo("GST-TEST");
        assertThat(captor.getValue().getPassportNumber()).isNull();
    }

    @Test
    void registersInternationalCarePartnerAsPending() throws Exception {
        CarePartnerRegistrationDto request =
                request("France", null, "PASSPORT-TEST", "image/jpeg");
        prepareRegistration();

        ArgumentCaptor<CarePartnerProfile> captor =
                ArgumentCaptor.forClass(CarePartnerProfile.class);
        when(partnerRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.registerCarePartner(request);

        assertThat(captor.getValue().getGstNumber()).isNull();
        assertThat(captor.getValue().getPassportNumber())
                .isEqualTo("PASSPORT-TEST");
    }

    @Test
    void requiresGstForIndianCarePartner() {
        CarePartnerRegistrationDto request =
                request("India", null, null, "application/pdf");

        assertThatThrownBy(() -> service.registerCarePartner(request))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo("GST_NUMBER_REQUIRED");
    }

    @Test
    void requiresPassportForInternationalCarePartner() {
        CarePartnerRegistrationDto request =
                request("France", null, null, "application/pdf");

        assertThatThrownBy(() -> service.registerCarePartner(request))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo("PASSPORT_REQUIRED");
    }

    @Test
    void requiresTradeLicenseForEveryCarePartner() {
        CarePartnerRegistrationDto request =
                request("India", "GST-TEST", null, "application/pdf");
        request.setTradeLicense(" ");

        assertThatThrownBy(() -> service.registerCarePartner(request))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo("TRADE_LICENSE_REQUIRED");
    }

    @Test
    void rejectsUnsupportedDocumentType() {
        CarePartnerRegistrationDto request =
                request("India", "GST-TEST", null, "image/png");

        assertThatThrownBy(() -> service.registerCarePartner(request))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo("INVALID_DOCUMENT_TYPE");
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
        CarePartnerProfile profile =
                profile(user, ApprovalStatus.APPROVED);
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

        var response = service.login(login());

        assertThat(response.getToken())
                .isEqualTo("test-token-not-logged");
        assertThat(response.getUserId()).isEqualTo(USER_ID);
        assertThat(response.getRole())
                .isEqualTo(RoleNames.CARE_PARTNER);
        assertThat(response.getPermissions()).isEmpty();
    }

    private void assertLoginDenied(
            ApprovalStatus status,
            String code
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

        assertThatThrownBy(() -> service.login(login()))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo(code);
    }

    private void prepareRegistration() throws Exception {
        when(roleRepository.findByName(RoleNames.CARE_PARTNER))
                .thenReturn(Optional.of(role()));
        when(passwordEncoder.encode("Test-password-1"))
                .thenReturn("encoded");
        when(fileStorageService.uploadFile(any(), any()))
                .thenReturn("/uploads/documents/generated.pdf");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private CarePartnerRegistrationDto request(
            String country,
            String gst,
            String passport,
            String contentType
    ) {
        CarePartnerRegistrationDto request =
                new CarePartnerRegistrationDto();
        request.setTitle("Mr");
        request.setFirstName("Test");
        request.setLastName("Partner");
        request.setEmail("partner@example.test");
        request.setPassword("Test-password-1");
        request.setCountry(country);
        request.setTradeLicense("TRADE-TEST");
        request.setGstNumber(gst);
        request.setPassportNumber(passport);
        request.setVerificationDocument(new MockMultipartFile(
                "verificationDocument",
                "ignored-client-name",
                contentType,
                new byte[]{1, 2, 3}
        ));
        return request;
    }

    private LoginRequestDto login() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("partner@example.test");
        request.setPassword("Test-password-1");
        return request;
    }

    private AuthenticatedUser principal() {
        return new AuthenticatedUser(
                USER_ID,
                "partner@example.test",
                "encoded",
                RoleNames.CARE_PARTNER,
                List.of(),
                true
        );
    }

    private User carePartnerUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail("partner@example.test");
        user.setRole(role());
        return user;
    }

    private Role role() {
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
