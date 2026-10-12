package com.mvt.backApp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String code;
    private String messageKey;
    private String message;
    private List<FieldErrorResponse> fieldErrors;
    private String path;
}