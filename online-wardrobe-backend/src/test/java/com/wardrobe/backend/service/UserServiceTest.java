package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldRegisterNewUser() {
        User user = new User();
        user.setUserName("newuser");
        user.setPassword("mypass1");
        user.setPhone("13600000000");
        user.setAddress("上海市");

        User registered = userService.register(user);

        assertNotNull(registered, "注册成功后应返回用户对象");
        assertNotNull(registered.getId(), "注册后应自动生成ID");
        assertEquals("newuser", registered.getUserName());
        assertEquals(2, registered.getRole(), "默认角色应为普通用户(2)");
        assertNotEquals("mypass1", registered.getPassword(), "密码应被加密存储");
    }

    @Test
    void shouldRejectDuplicateUsername() {
        User user1 = new User();
        user1.setUserName("duplicate");
        user1.setPassword("password1");
        user1.setRole(2);
        userService.register(user1);

        User user2 = new User();
        user2.setUserName("duplicate");
        user2.setPassword("password2");
        user2.setRole(2);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.register(user2),
                "重复用户名应抛出异常");
        assertTrue(ex.getMessage().contains("用户名已存在"));
    }

    @Test
    void shouldLoginWithCorrectCredentials() {
        User user = new User();
        user.setUserName("loginuser");
        user.setPassword("corrpwd1");
        user.setRole(2);
        userService.register(user);

        User loggedIn = userService.login("loginuser", "corrpwd1");

        assertNotNull(loggedIn, "正确凭证应能够登录");
        assertEquals("loginuser", loggedIn.getUserName());
    }

    @Test
    void shouldRejectWrongPassword() {
        User user = new User();
        user.setUserName("wrongpwuser");
        user.setPassword("rightp1");
        user.setRole(2);
        userService.register(user);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.login("wrongpwuser", "wrongp1"),
                "错误密码应抛出异常");
        assertTrue(ex.getMessage().contains("用户名或密码错误"));
    }

    @Test
    void shouldLoginByPhone() {
        User user = new User();
        user.setUserName("phoneLogin");
        user.setPassword("phone1");
        user.setPhone("13700000000");
        user.setRole(2);
        userService.register(user);

        User loggedIn = userService.loginByPhone("13700000000", "phone1");

        assertNotNull(loggedIn, "正确的手机号和密码应能登录");
        assertEquals("phoneLogin", loggedIn.getUserName());
    }
}
