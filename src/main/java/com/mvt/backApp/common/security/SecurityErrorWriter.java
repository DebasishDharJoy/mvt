package com.mvt.backApp.common.security;

import com.mvt.backApp.common.dto.ApiErrorResponse;
import com.mvt.backApp.common.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SecurityErrorWriter {

    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    public void write(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            String code,
            String messageKey
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .messageKey(messageKey)
                .message(messageService.getMessage(messageKey))
                .path(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}