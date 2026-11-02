package com.mvt.backApp.user.service;

import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.common.security.RoleNames;
import com.mvt.backApp.security.model.AuthenticatedUser;
import com.mvt.backApp.user.dto.CreateAdminRequest;
import com.mvt.backApp.user.entity.*;
import com.mvt.backApp.user.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {
    @Mock
    UserRepository users;
    @Mock
    RoleRepository roles;
    @Mock
    PermissionRepository permissions;
    @Mock
    PasswordEncoder encoder;
    @InjectMocks
    AdminUserService service;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void blocksDelegatingUnownedPermission() {
        AuthenticatedUser p = new AuthenticatedUser(UUID.randomUUID(), "admin@example.test", "x", RoleNames.ADMIN, List.of(new SimpleGrantedAuthority("CREATE_ADMIN")), true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p, null, p.getAuthorities()));
        CreateAdminRequest r = new CreateAdminRequest();
        r.setFirstName("A");
        r.setLastName("B");
        r.setGender(Gender.OTHER);
        r.setEmail("new@example.test");
        r.setTemporaryPassword("TemporaryPass1!");
        r.setPermissions(Set.of("ASSIGN_PERMISSIONS"));
        assertThatThrownBy(() -> service.createAdmin(r)).isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.getCode()).isEqualTo("PERMISSION_DELEGATION_FORBIDDEN"));
        verify(users, never()).save(any());
    }
}
