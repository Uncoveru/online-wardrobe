package com.wardrobe.backend.enums;

import java.util.Set;

/**
 * 角色-权限枚举：超级管理员(1) / 普通用户(2) / 操作员(3)
 */
public enum RolePermission {

    // 超级管理员：拥有全部权限
    SUPER_ADMIN(1, Set.of(
            Permission.CLOTHES_MANAGE,
            Permission.ORDERS_MANAGE,
            Permission.USERS_MANAGE
    )),
    // 普通用户：无管理权限
    USER(2, Set.of()),
    // 操作员：商品管理 + 订单管理（不可管理用户）
    OPERATOR(3, Set.of(
            Permission.CLOTHES_MANAGE,
            Permission.ORDERS_MANAGE
    ));

    // 权限常量
    public interface Permission {
        String CLOTHES_MANAGE = "clothes:manage";
        String ORDERS_MANAGE = "orders:manage";
        String USERS_MANAGE = "users:manage";
    }

    private final int roleId;
    private final Set<String> permissions;

    RolePermission(int roleId, Set<String> permissions) {
        this.roleId = roleId;
        this.permissions = permissions;
    }

    // 根据 roleId 获取对应角色（null 或未知角色默认返回 USER）
    public static RolePermission fromId(Integer roleId) {
        if (roleId == null) return USER;
        for (RolePermission rp : values()) {
            if (rp.roleId == roleId) return rp;
        }
        return USER;
    }

    // 是否拥有指定权限
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    // 是否为管理员（超级管理员或操作员）
    public boolean isAdmin() {
        return this != USER;
    }
}
