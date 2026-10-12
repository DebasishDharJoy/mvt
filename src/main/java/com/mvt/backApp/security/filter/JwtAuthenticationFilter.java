package com.mvt.backApp.security.filter;

import com.mvt.backApp.common.security.SecurityErrorWriter;
import com.mvt.backApp.security.model.AuthenticatedUser;
import com.mvt.backApp.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final SecurityErrorWriter errorWriter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.extractAllClaims(authHeader.substring(7));
            String email = claims.getSubject();

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<?> rawPermissions = claims.get("permissions", List.class);
                List<SimpleGrantedAuthority> authorities = rawPermissions == null
                        ? Collections.emptyList()
                        : rawPermissions.stream()
                                .map(String::valueOf)
                                .map(SimpleGrantedAuthority::new)
                                .toList();

                String userIdClaim = claims.get("userId", String.class);
                UUID userId = userIdClaim == null ? null : UUID.fromString(userIdClaim);
                String role = claims.get("role", String.class);

                AuthenticatedUser principal = new AuthenticatedUser(
                        userId,
                        email,
                        "",
                        role,
                        authorities,
                        true
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException exception) {
            SecurityContextHolder.clearContext();
            errorWriter.write(
                    request,
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "TOKEN_EXPIRED",
                    "error.token.expired"
            );
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            errorWriter.write(
                    request,
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "TOKEN_INVALID",
                    "error.token.invalid"
            );
        }
    }
}
