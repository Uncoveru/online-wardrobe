package com.wardrobe.backend.config;

import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.admin.seed.username:admin}")
    private String seedUsername;

    @Value("${app.admin.seed.password:admin123}")
    private String seedPassword;

    public DataInitializer(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

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
