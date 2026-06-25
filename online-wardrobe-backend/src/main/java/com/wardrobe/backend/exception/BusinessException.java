package com.wardrobe.backend.exception;

/**
 * 业务异常，携带自定义错误码
 */
public class BusinessException extends RuntimeException {

    // 业务错误码
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
