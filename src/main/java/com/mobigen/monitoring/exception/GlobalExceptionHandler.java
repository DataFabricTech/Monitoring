package com.mobigen.monitoring.exception;

import com.mobigen.monitoring.model.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(CustomException e)    {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.valueOf(e.getErrorCode().getStatus()))
                .body(MessageDto.builder().code(e.getErrorCode().getStatus())
                        .message("예상치 못한 에러가 발생하였습니다. " + e.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageDto.builder().code(500).message("예상치 못한 에러가 발생하였습니다. " + e.getMessage()).build());
    }
}
