package com.mvt.backApp.user.service;

import com.mvt.backApp.config.BootstrapProperties;
import com.mvt.backApp.user.entity.Permission;
import com.mvt.backApp.user.entity.Role;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.PermissionRepository;
import com.mvt.backApp.user.repository.RoleRepository;
import com.mvt.backApp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapServiceTest {

    @Mock PermissionRepository permissionRepository;
    @Mock RoleRepository roleRepository;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    private BootstrapProperties properties;
    private BootstrapService service;

    @BeforeEach
    void setUp() {
        properties = new BootstrapProperties();
        service = new BootstrapService(
                properties,
                permissionRepository,
                roleRepository,
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void doesNothingWhenDisabled() {
        service.initialize();

        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void repeatedBootstrapDoesNotOverwritePassword() {
        configureEnabledSuperAdmin();

        Map<String, Permission> permissions = new HashMap<>();
        Map<String, Role> roles = new HashMap<>();

        when(permissionRepository.findByName(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(
                        permissions.get(invocation.getArgument(0))
                ));
        when(permissionRepository.save(any(Permission.class)))
                .thenAnswer(invocation -> {
                    Permission permission = invocation.getArgument(0);
                    permissions.put(permission.getName(), permission);
                    return permission;
                });
        when(roleRepository.findByName(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(
                        roles.get(invocation.getArgument(0))
                ));
        when(roleRepository.save(any(Role.class)))
                .thenAnswer(invocation -> {
                    Role role = invocation.getArgument(0);
                    roles.put(role.getName(), role);
                    return role;
                });
        when(passwordEncoder.encode("Development-password-1"))
                .thenReturn("encoded");
        when(userRepository.existsByEmail("superadmin@example.test"))
                .thenReturn(false)
                .thenReturn(true);

        service.initialize();
        service.initialize();

        verify(passwordEncoder, times(1))
                .encode("Development-password-1");
        verify(userRepository, times(1))
                .save(any(User.class));
    }

    private void configureEnabledSuperAdmin() {
        properties.setEnabled(true);
        properties.getSuperAdmin().setEmail("superadmin@example.test");
        properties.getSuperAdmin().setPassword("Development-password-1");
        properties.getSuperAdmin().setFirstName("Test");
        properties.getSuperAdmin().setLastName("Administrator");
    }
}
