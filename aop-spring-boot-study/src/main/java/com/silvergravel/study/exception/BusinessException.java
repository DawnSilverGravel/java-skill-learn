package com.silvergravel.study.exception;

/**
 * @author DawnStar
 * @since : 2024/4/24
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String msg) {
        super(msg);
    }

}
