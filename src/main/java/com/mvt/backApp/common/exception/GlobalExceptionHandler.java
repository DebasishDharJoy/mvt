package com.mvt.backApp.common.exception;

import com.mvt.backApp.common.dto.*;
import com.mvt.backApp.common.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageService messages;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> api(ApiException ex, HttpServletRequest req) {
        return build(HttpStatus.valueOf(ex.getStatus()), ex.getCode(), ex.getMessageKey(),
                ex.getMessageArguments(), null, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex,
                                                       HttpServletRequest req) {
        List<FieldErrorResponse> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> field(error.getField(), validationKey(error), error.getArguments()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "error.validation.failed",
                new Object[0], fields, req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> credentials(BadCredentialsException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "error.auth.invalidCredentials",
                new Object[0], null, req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> denied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "error.access.denied",
                new Object[0], null, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> unexpected(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error path={}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "error.internal",
                new Object[0], null, req);
    }

    private FieldErrorResponse field(String field, String key, Object[] arguments) {
        MessageService.ResolvedMessage m = messages.resolve(key, arguments == null ? new Object[0] : arguments);
        return FieldErrorResponse.builder().field(field).code(key).messageKey(key)
                .defaultMessage(m.defaultMessage()).localizedMessage(m.localizedMessage()).build();
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String key, Object[] args,
                                                   List<FieldErrorResponse> fields, HttpServletRequest req) {
        MessageService.ResolvedMessage m = messages.resolve(key, args);
        return ResponseEntity.status(status).body(ApiErrorResponse.builder()
                .timestamp(Instant.now().toString()).status(status.value()).error(status.getReasonPhrase())
                .code(code).messageKey(key).defaultMessage(m.defaultMessage())
                .localizedMessage(m.localizedMessage()).requestedLanguage(m.requestedLanguage())
                .fieldErrors(fields).path(req.getRequestURI()).build());
    }

    private String validationKey(MessageSourceResolvable error) {
        String message = error.getDefaultMessage();
        if (message != null && message.startsWith("{") && message.endsWith("}"))
            return message.substring(1, message.length() - 1);
        String[] codes = error.getCodes();
        return codes != null && codes.length > 0 ? codes[codes.length - 1] : "validation.invalid";
    }
}
