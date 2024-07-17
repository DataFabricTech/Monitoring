package com.mobigen.monitoring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    CUSTOM_EXCEPTION_CODE_TEST(1000, "Test용 ErrorCode"),
    UNKNOWN(500, "알려지지 않는 Exception"),
    ;

    private final int status;
    private final String message;
}
