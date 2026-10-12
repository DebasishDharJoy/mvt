package com.mvt.backApp.common.exception;

import com.mvt.backApp.common.dto.ApiErrorResponse;
import com.mvt.backApp.common.dto.FieldErrorResponse;
import com.mvt.backApp.common.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatus());
        log.warn("API error code={} path={}", ex.getCode(), request.getRequestURI());
        return build(
                status,
                ex.getCode(),
                ex.getMessageKey(),
                messageService.getMessage(
                        ex.getMessageKey(),
                        ex.getMessageArguments()
                ),
                null,
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> FieldErrorResponse.builder()
                        .field(error.getField())
                        .code(resolveValidationCode(error))
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "error.validation.failed",
                messageService.getMessage("error.validation.failed"),
                fieldErrors,
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        log.warn("Failed login from IP={}", request.getRemoteAddr());
        return build(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "error.auth.invalidCredentials",
                messageService.getMessage("error.auth.invalidCredentials"),
                null,
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "error.access.denied",
                messageService.getMessage("error.access.denied"),
                null,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled error at path={}", request.getRequestURI(), ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "error.internal",
                messageService.getMessage("error.internal"),
                null,
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String code,
            String messageKey,
            String message,
            List<FieldErrorResponse> fieldErrors,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .messageKey(messageKey)
                .message(message)
                .fieldErrors(fieldErrors)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    private String resolveValidationCode(MessageSourceResolvable error) {
        Object[] arguments = error.getArguments();
        if (arguments != null) {
            for (Object argument : arguments) {
                if (argument instanceof MessageSourceResolvable resolvable) {
                    String[] codes = resolvable.getCodes();
                    if (codes != null && codes.length > 0) {
                        return codes[0];
                    }
                }
            }
        }
        String[] codes = error.getCodes();
        return codes != null && codes.length > 0
                ? codes[codes.length - 1]
                : "validation.invalid";
    }
}