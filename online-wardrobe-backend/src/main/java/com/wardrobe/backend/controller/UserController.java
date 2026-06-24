package com.wardrobe.backend.controller;

import com.wardrobe.backend.dto.Result;
import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.exception.BusinessException;
import com.wardrobe.backend.service.UserService;
import com.wardrobe.backend.utils.AuthUtils;
import com.wardrobe.backend.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody User user) {
        User registered = userService.register(user);
        String token = jwtUtils.generateToken(registered.getId(), registered.getUserName(), registered.getRole());
        registered.setPassword(null);
        return Result.ok(Map.of("user", registered, "token", token));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        User user = userService.login(account, password);
        String token = jwtUtils.generateToken(user.getId(), user.getUserName(), user.getRole());
        user.setPassword(null);
        return Result.ok(Map.of("user", user, "token", token));
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        userService.updateProfile(userId, body.get("phone"), body.get("address"));
        User updated = userService.getUserById(userId);
        updated.setPassword(null);
        return Result.ok(updated);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        userService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return Result.ok();
    }

    @PostMapping("/register-operator")
    public Result<Map<String, Object>> registerOperator(@RequestBody User user) {
        try {
            User registered = userService.registerOperator(user);
            registered.setPassword(null);
            return Result.ok(Map.of("message", "注册申请已提交，请等待超级管理员审核"));
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        }
    }
}
