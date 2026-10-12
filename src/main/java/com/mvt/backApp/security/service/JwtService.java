package com.mvt.backApp.security.service;

import com.mvt.backApp.security.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    private final String secret;
    private final long expirationTime;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationTime
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "app.jwt.secret must contain at least 32 UTF-8 bytes"
            );
        }
        this.secret = secret;
        this.expirationTime = expirationTime;
    }

    public String generateToken(AuthenticatedUser principal) {
        long now = System.currentTimeMillis();
        List<String> permissions = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        Map<String, Object> claims = Map.of(
                "userId", principal.getUserId().toString(),
                "role", principal.getRole(),
                "permissions", permissions
        );

        return Jwts.builder()
                .claims(claims)
                .subject(principal.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
