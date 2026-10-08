package com.mvt.backApp.common.exception;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ApiException extends RuntimeException{
    private String message;
    private int status;
    private String text;

    public ApiException(String message, int status, String text) {
        super(message);
        this.message = message;
        this.status = status;
        this.text = text;
    }
}

