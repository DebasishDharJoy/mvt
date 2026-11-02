package com.mvt.backApp.common.service;
import com.mvt.backApp.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class ApiResponseFactory {
    private final MessageService messages;
    public <T> ApiResponse<T> success(String key, int status, T data, Object... arguments) {
        MessageService.ResolvedMessage m = messages.resolve(key, arguments);
        return ApiResponse.<T>builder().messageKey(m.messageKey())
                .defaultMessage(m.defaultMessage()).localizedMessage(m.localizedMessage())
                .requestedLanguage(m.requestedLanguage()).status(status)
                .success("success").data(data).build();
    }
}
