package com.wardrobe.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全配置：提供 BCrypt 密码编码器 Bean
 */
@Configuration
public class SecurityConfig {

    // BCrypt 密码编码器，用于用户密码的加密存储与校验
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
