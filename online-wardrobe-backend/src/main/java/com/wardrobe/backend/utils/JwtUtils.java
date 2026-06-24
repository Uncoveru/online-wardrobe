package com.wardrobe.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtils(String secret, long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public JwtUtils(String secret) {
        this(secret, 3600000);
    }

    public String generateToken(Integer userId, String userName, Integer role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("userName", userName)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Integer getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Integer.class);
    }

    public String getUserName(String token) {
        Claims claims = parseToken(token);
        return claims.get("userName", String.class);
    }

    public Integer getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", Integer.class);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
