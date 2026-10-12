package com.mvt.backApp.security.audit;

import com.mvt.backApp.security.model.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class AuditLoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() { }

    @Before("controllerMethods()")
    public void logApiAccess(JoinPoint joinPoint) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = null;
        String username = "ANONYMOUS";

        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
            if (authentication.getPrincipal() instanceof AuthenticatedUser principal) {
                userId = principal.getUserId();
            }
        }

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = request.getRemoteAddr();
        } else if (ipAddress.contains(",")) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(',')).trim();
        }

        log.info(
                "AUDIT timestamp={} userId={} username={} ip={} method={} path={} action={}",
                Instant.now(),
                userId,
                username,
                ipAddress,
                request.getMethod(),
                request.getRequestURI(),
                joinPoint.getSignature().toShortString()
        );
    }
}
