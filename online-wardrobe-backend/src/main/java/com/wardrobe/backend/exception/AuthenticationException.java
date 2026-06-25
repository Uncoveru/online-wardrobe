package com.wardrobe.backend.exception;

/**
 * 认证异常 → HTTP 401
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
