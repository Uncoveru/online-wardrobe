package com.wardrobe.backend.utils;

import com.wardrobe.backend.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;

public class AuthUtils {

    private AuthUtils() {
    }

    public static Integer getUserId(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            throw new AuthenticationException("未登录或登录已过期");
        }
        return userId;
    }

    public static Integer getRole(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null) {
            throw new AuthenticationException("未登录或登录已过期");
        }
        return role;
    }
}
