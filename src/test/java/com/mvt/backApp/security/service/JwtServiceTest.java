package com.mvt.backApp.security.service;

import com.mvt.backApp.security.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "0123456789abcdef0123456789abcdef";

    private static final UUID USER_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    void generatedTokenContainsRequiredClaims() {
        JwtService service = new JwtService(SECRET, 86_400_000L);

        AuthenticatedUser principal = new AuthenticatedUser(
                USER_ID,
                "user@example.test",
                "hash",
                "CARE_PARTNER",
                List.of(new SimpleGrantedAuthority("VIEW_REPORTS")),
                true
        );

        String token = service.generateToken(principal);
        Claims claims = service.extractAllClaims(token);

        assertThat(claims.getSubject())
                .isEqualTo("user@example.test");
        assertThat(claims.get("userId", String.class))
                .isEqualTo(USER_ID.toString());
        assertThat(claims.get("role"))
                .isEqualTo("CARE_PARTNER");
        assertThat(claims.get("permissions", List.class))
                .containsExactly("VIEW_REPORTS");
    }
}
