package com.mvt.backApp.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String code;
    private final String messageKey;
    private final int status;
    private final Object[] messageArguments;

    public ApiException(String code, String messageKey, int status, Object... arguments) {
        super(messageKey);
        this.code = code;
        this.messageKey = messageKey;
        this.status = status;
        this.messageArguments = arguments == null ? new Object[0] : arguments.clone();
    }
}
