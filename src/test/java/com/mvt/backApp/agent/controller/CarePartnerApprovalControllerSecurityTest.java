package com.mvt.backApp.agent.controller;

import com.mvt.backApp.agent.service.CarePartnerApprovalService;
import com.mvt.backApp.common.security.PermissionNames;
import com.mvt.backApp.common.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CarePartnerApprovalControllerSecurityTest.TestConfig.class)
class CarePartnerApprovalControllerSecurityTest {

    @org.springframework.beans.factory.annotation.Autowired
    CarePartnerApprovalController controller;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void approvalIsDeniedWithoutApprovePermission() {
        authenticate(PermissionNames.CARE_PARTNER_APPLICATION_VIEW);

        assertThatThrownBy(() -> controller.approve("AGT-TEST"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void approvalPermissionPassesMethodSecurity() {
        authenticate(PermissionNames.CARE_PARTNER_APPLICATION_APPROVE);

        assertThat(controller.approve("AGT-TEST").getStatusCode().value())
                .isEqualTo(200);
    }

    private void authenticate(String authority) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test-admin",
                        null,
                        java.util.List.of(new SimpleGrantedAuthority(authority))
                )
        );
    }

    @Configuration
    @EnableMethodSecurity
    static class TestConfig {

        @Bean
        CarePartnerApprovalService carePartnerApprovalService() {
            return mock(CarePartnerApprovalService.class);
        }

        @Bean
        MessageService messageService() {
            return mock(MessageService.class);
        }

        @Bean
        CarePartnerApprovalController carePartnerApprovalController(
                CarePartnerApprovalService service,
                MessageService messageService
        ) {
            return new CarePartnerApprovalController(service, messageService);
        }
    }
}
