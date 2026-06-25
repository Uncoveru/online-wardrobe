package com.wardrobe.backend.exception;

/**
 * 权限拒绝异常 → HTTP 403
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
