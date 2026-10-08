package com.mvt.backApp.security.audit;

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

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AuditLoggingAspect {

    // Pointcut targeting all methods within controllers
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logApiAccess(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String username = (authentication != null && authentication.isAuthenticated())
                    ? authentication.getName()
                    : "ANONYMOUS";

            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }

            log.info("AUDIT | Timestamp: {} | User: {} | IP: {} | Method: {} | Path: {} | Action: {}",
                    LocalDateTime.now(),
                    username,
                    ipAddress,
                    request.getMethod(),
                    request.getRequestURI(),
                    joinPoint.getSignature().toShortString()
            );
        }
    }
}
