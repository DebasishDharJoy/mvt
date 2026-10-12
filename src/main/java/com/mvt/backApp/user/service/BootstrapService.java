package com.mvt.backApp.user.service;

import com.mvt.backApp.common.security.PermissionNames;
import com.mvt.backApp.common.security.RoleNames;
import com.mvt.backApp.config.BootstrapProperties;
import com.mvt.backApp.user.entity.Permission;
import com.mvt.backApp.user.entity.Role;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.PermissionRepository;
import com.mvt.backApp.user.repository.RoleRepository;
import com.mvt.backApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BootstrapService {
    private final BootstrapProperties properties;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void initialize() {
        if (!properties.isEnabled()) {
            return;
        }
        validateAccount(properties.getSuperAdmin(), "super-admin");

        Set<Permission> allPermissions = new HashSet<>();
        for (String name : PermissionNames.ALL) {
            allPermissions.add(findOrCreatePermission(name));
        }

        Role superAdminRole = findOrCreateRole(RoleNames.SUPER_ADMIN);
        superAdminRole.setPermissions(allPermissions);
        roleRepository.save(superAdminRole);

        findOrCreateRole(RoleNames.PATIENT);
        findOrCreateRole(RoleNames.CARE_PARTNER);
        Role adminRole = findOrCreateRole(RoleNames.ADMIN);

        createAccountIfMissing(properties.getSuperAdmin(), superAdminRole);

        if (properties.getAdmin().isEnabled()) {
            validateAccount(properties.getAdmin(), "admin");
            Set<String> requested = properties.getAdmin().getPermissions();
            if (requested == null || requested.isEmpty()) {
                requested = PermissionNames.DEFAULT_DELEGATED_ADMIN;
            }
            if (!PermissionNames.ALL.containsAll(requested)) {
                Set<String> invalid = new HashSet<>(requested);
                invalid.removeAll(PermissionNames.ALL);
                throw new IllegalStateException("Unknown delegated-admin permissions: " + invalid);
            }
            Set<Permission> delegated = new HashSet<>();
            for (String name : requested) {
                delegated.add(findOrCreatePermission(name));
            }
            adminRole.setPermissions(delegated);
            roleRepository.save(adminRole);
            createAccountIfMissing(properties.getAdmin(), adminRole);
        }
    }

    private Permission findOrCreatePermission(String name) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setName(name);
            permission.setActive(true);
            permission.setDeleted(false);
            return permissionRepository.save(permission);
        });
    }

    private Role findOrCreateRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setActive(true);
            role.setDeleted(false);
            return roleRepository.save(role);
        });
    }

    private void createAccountIfMissing(BootstrapProperties.Account account, Role role) {
        String email = normalizeEmail(account.getEmail());
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(account.getPassword()));
        user.setFirstName(account.getFirstName().trim());
        user.setLastName(account.getLastName().trim());
        user.setRole(role);
        user.setActive(true);
        user.setDeleted(false);
        userRepository.save(user);
    }

    private void validateAccount(BootstrapProperties.Account account, String label) {
        if (isBlank(account.getEmail())
                || isBlank(account.getPassword())
                || isBlank(account.getFirstName())
                || isBlank(account.getLastName())) {
            throw new IllegalStateException(
                    "Bootstrap " + label + " email, password, first-name and last-name are required"
            );
        }
        if (account.getPassword().length() < 12) {
            throw new IllegalStateException("Bootstrap " + label + " password must have at least 12 characters");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
