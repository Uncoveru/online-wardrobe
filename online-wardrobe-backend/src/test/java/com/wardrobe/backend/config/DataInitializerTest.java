package com.wardrobe.backend.config;

import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DataInitializerTest {

    @Autowired
    private DataInitializer initializer;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void clearAdmins() {
        User existing = userMapper.findByUserName("admin");
        if (existing != null) {
            userMapper.deleteById(existing.getId());
        }
    }

    @Test
    void shouldCreateAdminWhenNoneExists() {
        assertEquals(0, userMapper.countByRole(1));

        initializer.run();

        assertEquals(1, userMapper.countByRole(1));
    }

    @Test
    void shouldNotDuplicateAdminWhenAlreadyExists() {
        User admin = new User();
        admin.setUserName("existingadmin");
        admin.setPassword(passwordEncoder.encode("adminp1"));
        admin.setRole(1);
        admin.setStatus(1);
        userMapper.insert(admin);
        assertEquals(1, userMapper.countByRole(1));

        initializer.run();

        assertEquals(1, userMapper.countByRole(1));
    }
}
