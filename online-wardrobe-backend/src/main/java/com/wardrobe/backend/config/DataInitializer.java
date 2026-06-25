package com.wardrobe.backend.config;

import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时初始化管理员账号（仅当不存在管理员时）
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    // 管理员用户名（application.yml: app.admin.seed.username，默认 admin）
    @Value("${app.admin.seed.username:admin}")
    private String seedUsername;

    // 管理员密码（application.yml: app.admin.seed.password，默认 admin123）
    @Value("${app.admin.seed.password:admin123}")
    private String seedPassword;

    public DataInitializer(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 启动后检查：若管理员不存在则创建默认管理员（role=1, status=1）
    @Override
    public void run(String... args) {
        if (userMapper.countByRole(1) > 0) {
            return;
        }

        User admin = new User();
        admin.setUserName(seedUsername);
        admin.setPassword(passwordEncoder.encode(seedPassword));
        admin.setRole(1);
        admin.setStatus(1);
        userMapper.insert(admin);
    }
}
