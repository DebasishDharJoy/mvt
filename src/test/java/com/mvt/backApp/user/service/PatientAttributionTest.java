package com.mvt.backApp.user.service;

import com.mvt.backApp.agent.entity.*;
import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.id.*;
import com.mvt.backApp.common.security.RoleNames;
import com.mvt.backApp.common.service.FileStorageService;
import com.mvt.backApp.patient.dto.PatientRegistrationDto;
import com.mvt.backApp.patient.entity.PatientProfile;
import com.mvt.backApp.patient.repository.PatientProfileRepository;
import com.mvt.backApp.security.model.AuthenticatedUser;
import com.mvt.backApp.security.service.JwtService;
import com.mvt.backApp.user.entity.*;
import com.mvt.backApp.user.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientAttributionTest {
    @Mock
    FileStorageService storage;
    @Mock
    PasswordEncoder encoder;
    @Mock
    RoleRepository roles;
    @Mock
    UserRepository users;
    @Mock
    CarePartnerProfileRepository partners;
    @Mock
    PatientProfileRepository patients;
    @Mock
    AuthenticationManager manager;
    @Mock
    JwtService jwt;
    @Mock
    EmailOtpService otp;
    @Mock
    BusinessIdService ids;
    @InjectMocks
    AuthService service;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void selfRegistrationHasNoReferrer() {
        stub();
        var response = service.registerPatient(request());
        ArgumentCaptor<PatientProfile> c = ArgumentCaptor.forClass(PatientProfile.class);
        verify(patients).save(c.capture());
        assertThat(c.getValue().getReferredBy()).isNull();
        assertThat(c.getValue().getReferredAt()).isNull();
        assertThat(response.getReferredByPartnerId()).isNull();
    }

    @Test
    void partnerCreationStoresReferrerAndTimestamp() {
        stub();
        UUID id = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(id, "agent@example.test", "x", RoleNames.CARE_PARTNER, List.of(), true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, List.of()));
        User agent = new User();
        agent.setId(id);
        CarePartnerProfile partner = new CarePartnerProfile();
        partner.setPartnerId("AGTIN1001");
        partner.setApprovalStatus(ApprovalStatus.APPROVED);
        when(users.findById(id)).thenReturn(Optional.of(agent));
        when(partners.findByUser(agent)).thenReturn(Optional.of(partner));
        var response = service.registerPatientForCurrentPartner(request());
        ArgumentCaptor<PatientProfile> c = ArgumentCaptor.forClass(PatientProfile.class);
        verify(patients).save(c.capture());
        assertThat(c.getValue().getReferredBy()).isSameAs(partner);
        assertThat(c.getValue().getReferredAt()).isNotNull();
        assertThat(response.getReferredByPartnerId()).isEqualTo("AGTIN1001");
    }

    private void stub() {
        Role r = new Role();
        r.setName(RoleNames.PATIENT);
        when(roles.findByName(RoleNames.PATIENT)).thenReturn(Optional.of(r));
        when(users.existsByEmailIgnoreCase("patient@example.test")).thenReturn(false);
        when(users.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(ids.next(BusinessIdKind.PATIENT, "IN")).thenReturn("PATIN1001");
    }

    private PatientRegistrationDto request() {
        PatientRegistrationDto d = new PatientRegistrationDto();
        d.setFirstName("Test");
        d.setLastName("Patient");
        d.setEmail("patient@example.test");
        d.setGender(Gender.OTHER);
        d.setPassword("StrongPass1!");
        d.setConfirmPassword("StrongPass1!");
        d.setCountryCode("IN");
        d.setMobileNumber("+919876543210");
        d.setWhatsappSameAsMobile(true);
        d.setEmailOtp("123456");
        return d;
    }
}
