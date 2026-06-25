package com.wardrobe.backend.controller;

import com.wardrobe.backend.dto.Result;
import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.enums.RolePermission;
import com.wardrobe.backend.exception.ForbiddenException;
import com.wardrobe.backend.service.UserService;
import com.wardrobe.backend.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员用户管理：列表 / 新增 / 编辑 / 删除 / 审核
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // 用户列表（支持按用户名/手机号搜索）
    @GetMapping
    public Result<List<User>> listUsers(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.USERS_MANAGE);
        if (userName == null && phone == null) {
            return Result.ok(userService.getAllUsers());
        }
        List<User> users = userService.searchUsers(userName, phone);
        return Result.ok(users);
    }

    // 管理员新增用户
    @PostMapping
    public Result<User> addUser(@RequestBody User user, HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.USERS_MANAGE);
        User saved = userService.addUserByAdmin(user);
        return Result.ok(saved);
    }

    // 编辑用户信息
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Integer id, @RequestBody User user,
                                   HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.USERS_MANAGE);
        user.setId(id);
        User updated = userService.updateUserByAdmin(user);
        updated.setPassword(null);
        return Result.ok(updated);
    }

    // 删除用户（不能删除自己）
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.USERS_MANAGE);
        Integer currentUserId = AuthUtils.getUserId(request);
        if (id.equals(currentUserId)) {
            throw new ForbiddenException("不能删除当前登录的管理员账号");
        }
        userService.deleteUser(id);
        return Result.ok();
    }

    // 审核通过
    @PutMapping("/{id}/approve")
    public Result<Void> approveUser(@PathVariable Integer id, HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.USERS_MANAGE);
        userService.approveUser(id);
        return Result.ok();
    }

    // 审核拒绝
    @PutMapping("/{id}/reject")
    public Result<Void> rejectUser(@PathVariable Integer id, HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.USERS_MANAGE);
        userService.rejectUser(id);
        return Result.ok();
    }

    // 撤销拒绝（恢复为通过状态）
    @PutMapping("/{id}/undo-reject")
    public Result<Void> undoRejectUser(@PathVariable Integer id, HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.USERS_MANAGE);
        userService.approveUser(id);
        return Result.ok();
    }

    // 权限校验：当前请求角色是否拥有指定权限
    private void requirePermission(HttpServletRequest request, String permission) {
        Integer role = AuthUtils.getRole(request);
        if (!RolePermission.fromId(role).hasPermission(permission)) {
            throw new ForbiddenException("无权限，仅管理员可操作");
        }
    }
}
