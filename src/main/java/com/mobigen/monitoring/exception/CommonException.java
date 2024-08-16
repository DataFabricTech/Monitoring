package com.mobigen.monitoring.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonException extends RuntimeException{
    private final ErrorCode errorCode;

    public String getMessage() {
        return errorCode.getMessage();
    }
}
