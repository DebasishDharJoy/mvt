package com.mvt.backApp.common.dto;
import lombok.*;
        import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApiErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String code;
    private String messageKey;
    private String defaultMessage;
    private String localizedMessage;
    private String requestedLanguage;
    private List<FieldErrorResponse> fieldErrors;
    private String path;
}
