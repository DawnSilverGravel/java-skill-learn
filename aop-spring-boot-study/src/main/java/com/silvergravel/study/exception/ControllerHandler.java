package com.silvergravel.study.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author DawnStar
 * @since : 2024/4/24
 */
@RestControllerAdvice
public class ControllerHandler {
    @ExceptionHandler(BusinessException.class)
    public String business(BusinessException exception) {
        return exception.getMessage();
    }
}
