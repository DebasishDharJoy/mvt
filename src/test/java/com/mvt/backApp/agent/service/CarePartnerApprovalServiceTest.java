package com.mvt.backApp.agent.service;

import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.security.model.AuthenticatedUser;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarePartnerApprovalServiceTest {

    private static final UUID ADMIN_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    CarePartnerProfileRepository repository;

    @Mock
    UserRepository userRepository;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authorizedAdministratorApprovesPendingApplication() {
        CarePartnerProfile profile = pendingProfile();
        User administrator = administrator();

        when(repository.findByPartnerId("AGT-TEST"))
                .thenReturn(Optional.of(profile));
        when(userRepository.findById(ADMIN_ID))
                .thenReturn(Optional.of(administrator));

        authenticate(ADMIN_ID);

        CarePartnerApprovalService service =
                new CarePartnerApprovalService(repository, userRepository);

        var response = service.approve("AGT-TEST");

        assertThat(response.getApprovalStatus())
                .isEqualTo(ApprovalStatus.APPROVED);
        assertThat(profile.getApprovedBy())
                .isSameAs(administrator);
        assertThat(profile.getApprovedBy().getId())
                .isEqualTo(ADMIN_ID);
        assertThat(profile.getApprovedAt()).isNotNull();

        verify(userRepository).findById(ADMIN_ID);
        verify(repository).save(profile);
    }

    @Test
    void rejectionReasonIsRequired() {
        CarePartnerApprovalService service =
                new CarePartnerApprovalService(repository, userRepository);

        assertThatThrownBy(() -> service.reject("AGT-TEST", " "))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo("REJECTION_REASON_REQUIRED");
    }

    @Test
    void rejectedApplicationCannotBeReviewedAgain() {
        CarePartnerProfile profile = pendingProfile();
        profile.setApprovalStatus(ApprovalStatus.REJECTED);

        when(repository.findByPartnerId("AGT-TEST"))
                .thenReturn(Optional.of(profile));

        authenticate(ADMIN_ID);

        CarePartnerApprovalService service =
                new CarePartnerApprovalService(repository, userRepository);

        assertThatThrownBy(() -> service.approve("AGT-TEST"))
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo("CARE_PARTNER_APPLICATION_ALREADY_REVIEWED");
    }

    private CarePartnerProfile pendingProfile() {
        User user = new User();
        user.setEmail("partner@example.test");
        user.setFirstName("Test");
        user.setLastName("Partner");

        CarePartnerProfile profile = new CarePartnerProfile();
        profile.setPartnerId("AGT-TEST");
        profile.setUser(user);
        profile.setApprovalStatus(ApprovalStatus.PENDING);
        return profile;
    }

    private User administrator() {
        User user = new User();
        user.setId(ADMIN_ID);
        user.setEmail("admin@example.test");
        user.setFirstName("Test");
        user.setLastName("Administrator");
        return user;
    }

    private void authenticate(UUID userId) {
        AuthenticatedUser principal = new AuthenticatedUser(
                userId,
                "admin@example.test",
                "",
                "ADMIN",
                List.of(),
                true
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of())
        );
    }
}
