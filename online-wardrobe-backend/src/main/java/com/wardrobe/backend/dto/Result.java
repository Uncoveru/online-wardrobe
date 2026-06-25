package com.wardrobe.backend.dto;

/**
 * 统一 API 响应体：code + message + data
 */
public class Result<T> {

    // HTTP 状态码
    private int code;
    // 提示消息
    private String message;
    // 响应数据
    private T data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    // 成功响应（带数据），code=200
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    // 成功响应（无数据）
    public static <T> Result<T> ok() {
        return ok(null);
    }

    // 失败响应（指定状态码和消息）
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    // 失败响应（默认 code=500）
    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }
}
