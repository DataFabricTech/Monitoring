package com.mobigen.monitoring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Method;

@Slf4j
@RestControllerAdvice
public class GlobalAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("[ASYNC-ERROR] method: {}, exception: {}, exception message: {}", method.getName(), ex, ex.getMessage());
    }
}
