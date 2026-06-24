package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceAdditionalTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldChangePasswordWithCorrectOldPassword() {
        User user = new User();
        user.setUserName("pwchange");
        user.setPassword("oldpw1");
        user.setRole(2);
        userService.register(user);

        userService.changePassword(user.getId(), "oldpw1", "newpw1");

        User loggedIn = userService.login("pwchange", "newpw1");
        assertNotNull(loggedIn);
    }

    @Test
    void shouldRejectWrongOldPassword() {
        User user = new User();
        user.setUserName("pwwrong");
        user.setPassword("corrp1");
        user.setRole(2);
        userService.register(user);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.changePassword(user.getId(), "wrongp1", "newpw1"));
        assertTrue(ex.getMessage().contains("原密码错误"));
    }

    @Test
    void shouldRejectDuplicatePhone() {
        User user1 = new User();
        user1.setUserName("phoneuser1");
        user1.setPassword("test123");
        user1.setPhone("13911112222");
        user1.setRole(2);
        userService.register(user1);

        User user2 = new User();
        user2.setUserName("phoneuser2");
        user2.setPassword("test456");
        user2.setPhone("13911112222");
        user2.setRole(2);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.register(user2));
        assertTrue(ex.getMessage().contains("手机号已被注册"));
    }

    @Test
    void shouldAllowRegisterWithoutPhone() {
        User user = new User();
        user.setUserName("nophoneuser");
        user.setPassword("test123");
        user.setRole(2);
        User registered = userService.register(user);
        assertNotNull(registered.getId());
    }

    @Test
    void shouldRejectWeakPasswordTooShort() {
        User user = new User();
        user.setUserName("weakpw1");
        user.setPassword("a1");
        user.setRole(2);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.register(user));
        assertTrue(ex.getMessage().contains("密码"));
    }

    @Test
    void shouldRejectWeakPasswordNoDigit() {
        User user = new User();
        user.setUserName("weakpw2");
        user.setPassword("abcdef");
        user.setRole(2);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.register(user));
        assertTrue(ex.getMessage().contains("密码"));
    }

    @Test
    void shouldRejectWeakPasswordNoLetter() {
        User user = new User();
        user.setUserName("weakpw3");
        user.setPassword("123456");
        user.setRole(2);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.register(user));
        assertTrue(ex.getMessage().contains("密码"));
    }

    @Test
    void shouldAcceptStrongPassword() {
        User user = new User();
        user.setUserName("strongpw");
        user.setPassword("abc123");
        user.setRole(2);
        User registered = userService.register(user);
        assertNotNull(registered.getId());
    }

    @Test
    void shouldValidatePasswordWhenChanging() {
        User user = new User();
        user.setUserName("changepw");
        user.setPassword("old123");
        user.setRole(2);
        userService.register(user);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.changePassword(user.getId(), "old123", "12"));
        assertTrue(ex.getMessage().contains("密码"));
    }
}
