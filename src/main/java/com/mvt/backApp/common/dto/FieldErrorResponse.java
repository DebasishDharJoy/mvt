package com.mvt.backApp.common.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorResponse {
    private String field;
    private String code;
    private String messageKey;
    private String defaultMessage;
    private String localizedMessage;
}
