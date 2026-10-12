package com.mvt.backApp.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AuthenticatedUser implements UserDetails {
    private final UUID userId;
    private final String email;
    private final String password;
    private final String role;
    private final List<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public AuthenticatedUser(
            UUID userId,
            String email,
            String password,
            String role,
            List<? extends GrantedAuthority> authorities,
            boolean enabled
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = List.copyOf(authorities);
        this.enabled = enabled;
    }

    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
