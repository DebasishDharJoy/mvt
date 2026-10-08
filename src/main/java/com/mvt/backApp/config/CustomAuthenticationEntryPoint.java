package com.mvt.backApp.config;


import com.mvt.backApp.common.dto.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 1. Set the HTTP response status to 401 Unauthorized
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 2. Tell the client we are returning JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 3. Build your standardized ApiErrorResponse
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Full authentication is required to access this resource. Please provide a valid token.")
                .path(request.getRequestURI())
                .build();

        // 4. Write the JSON object to the HTTP response body
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
