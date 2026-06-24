package com.wardrobe.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolePermissionTest {

    @Test
    void superAdminShouldHaveAllPermissions() {
        RolePermission role = RolePermission.fromId(1);
        assertEquals(RolePermission.SUPER_ADMIN, role);
        assertTrue(role.hasPermission(RolePermission.Permission.CLOTHES_MANAGE));
        assertTrue(role.hasPermission(RolePermission.Permission.ORDERS_MANAGE));
        assertTrue(role.hasPermission(RolePermission.Permission.USERS_MANAGE));
        assertTrue(role.isAdmin());
    }

    @Test
    void userShouldHaveNoPermissions() {
        RolePermission role = RolePermission.fromId(2);
        assertEquals(RolePermission.USER, role);
        assertFalse(role.hasPermission(RolePermission.Permission.CLOTHES_MANAGE));
        assertFalse(role.hasPermission(RolePermission.Permission.ORDERS_MANAGE));
        assertFalse(role.hasPermission(RolePermission.Permission.USERS_MANAGE));
        assertFalse(role.isAdmin());
    }

    @Test
    void operatorShouldHaveOnlyClothesAndOrders() {
        RolePermission role = RolePermission.fromId(3);
        assertEquals(RolePermission.OPERATOR, role);
        assertTrue(role.hasPermission(RolePermission.Permission.CLOTHES_MANAGE));
        assertTrue(role.hasPermission(RolePermission.Permission.ORDERS_MANAGE));
        assertFalse(role.hasPermission(RolePermission.Permission.USERS_MANAGE));
        assertTrue(role.isAdmin());
    }

    @Test
    void nullRoleShouldDefaultToUser() {
        RolePermission role = RolePermission.fromId(null);
        assertEquals(RolePermission.USER, role);
        assertFalse(role.isAdmin());
    }

    @Test
    void unknownRoleIdShouldDefaultToUser() {
        RolePermission role = RolePermission.fromId(99);
        assertEquals(RolePermission.USER, role);
        assertFalse(role.isAdmin());
    }
}
