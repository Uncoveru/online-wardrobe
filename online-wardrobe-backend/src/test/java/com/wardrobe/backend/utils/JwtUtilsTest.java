package com.wardrobe.backend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils("test-secret-key-for-jwt-which-is-long-enough");
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtUtils.generateToken(1, "testuser", 2);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldParseUserIdFromToken() {
        String token = jwtUtils.generateToken(42, "zhangsan", 1);
        Integer userId = jwtUtils.getUserId(token);
        assertEquals(42, userId);
    }

    @Test
    void shouldParseUserNameFromToken() {
        String token = jwtUtils.generateToken(1, "lisi", 2);
        String userName = jwtUtils.getUserName(token);
        assertEquals("lisi", userName);
    }

    @Test
    void shouldParseRoleFromToken() {
        String token = jwtUtils.generateToken(1, "admin", 1);
        Integer role = jwtUtils.getRole(token);
        assertEquals(1, role);
    }

    @Test
    void shouldValidateValidToken() {
        String token = jwtUtils.generateToken(1, "admin", 1);
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void shouldRejectExpiredToken() throws Exception {
        JwtUtils shortLived = new JwtUtils("this-is-a-long-enough-secret-for-jwt-testing-purposes", 1);
        String token = shortLived.generateToken(1, "test", 2);
        Thread.sleep(1100);
        assertFalse(shortLived.validateToken(token));
    }

    @Test
    void shouldRejectTamperedToken() {
        String token = jwtUtils.generateToken(1, "admin", 1);
        String tampered = token.substring(0, token.length() - 3) + "xxx";
        assertFalse(jwtUtils.validateToken(tampered));
    }
}
