package com.wardrobe.backend.exception;

import com.wardrobe.backend.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一将异常转为 Result 响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 认证异常 → 401
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuth(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return Result.fail(401, e.getMessage());
    }

    // 权限拒绝 → 403
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleForbidden(ForbiddenException e) {
        log.warn("权限拒绝: {}", e.getMessage());
        return Result.fail(403, e.getMessage());
    }

    // 业务异常：根据错误码映射 HTTP 状态码（5xx 记 error 日志，其他记 warn）
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException e) {
        HttpStatus status = HttpStatus.resolve(e.getCode());
        if (status == null) {
            status = HttpStatus.BAD_REQUEST;
        }
        if (status.is5xxServerError()) {
            log.error("业务异常 ({}): {}", e.getCode(), e.getMessage());
        } else {
            log.warn("业务异常 ({}): {}", e.getCode(), e.getMessage());
        }
        return ResponseEntity.status(status)
                .body(Result.fail(e.getCode(), e.getMessage()));
    }

    // 兜底：未捕获异常 → 500
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleUnknown(Exception e) {
        log.error("未捕获的服务器错误", e);
        return Result.fail(500, "服务器内部错误");
    }
}
