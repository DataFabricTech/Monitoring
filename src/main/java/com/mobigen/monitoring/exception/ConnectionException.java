package com.mobigen.monitoring.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConnectionException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public String getMessage() {
        return errorCode.getMessage() + ": " + message;
    }
}
