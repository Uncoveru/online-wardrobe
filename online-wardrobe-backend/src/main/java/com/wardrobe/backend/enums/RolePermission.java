package com.wardrobe.backend.enums;

import java.util.Set;

public enum RolePermission {
    SUPER_ADMIN(1, Set.of(
            Permission.CLOTHES_MANAGE,
            Permission.ORDERS_MANAGE,
            Permission.USERS_MANAGE
    )),
    USER(2, Set.of()),
    OPERATOR(3, Set.of(
            Permission.CLOTHES_MANAGE,
            Permission.ORDERS_MANAGE
    ));

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

    public static RolePermission fromId(Integer roleId) {
        if (roleId == null) return USER;
        for (RolePermission rp : values()) {
            if (rp.roleId == roleId) return rp;
        }
        return USER;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean isAdmin() {
        return this != USER;
    }
}
