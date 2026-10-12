package com.mvt.backApp.security.service;

import com.mvt.backApp.security.model.AuthenticatedUser;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found"
                ));

        if (user.getRole() == null) {
            throw new UsernameNotFoundException("User role is not assigned");
        }

        List<SimpleGrantedAuthority> authorities = user.getRole()
                .getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .toList();

        return new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().getName(),
                authorities,
                user.isActive() && !user.isDeleted()
        );
    }
}
