package com.mvt.backApp.common.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApiResponse<T> {
    private String messageKey;
    private String defaultMessage;
    private String localizedMessage;
    private String requestedLanguage;
    private int status;
    private String success;
    private T data;
}
