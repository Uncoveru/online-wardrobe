package com.wardrobe.backend.controller;

import com.wardrobe.backend.dto.Result;
import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.service.UserService;
import com.wardrobe.backend.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员登录接口
 */
@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AdminAuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    // 管理员登录，返回用户信息 + JWT Token
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        User user = userService.loginAdmin(account, password);
        String token = jwtUtils.generateToken(user.getId(), user.getUserName(), user.getRole());
        user.setPassword(null);
        return Result.ok(Map.of("user", user, "token", token));
    }
}
