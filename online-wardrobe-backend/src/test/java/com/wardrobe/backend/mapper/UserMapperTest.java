package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldInsertUser() {
        User user = new User();
        user.setUserName("testuser");
        user.setPassword("123456");
        user.setPhone("13900000000");
        user.setAddress("测试地址");
        user.setRole(2);

        userMapper.insert(user);

        assertNotNull(user.getId(), "插入后应自动生成 ID");
    }

    @Test
    void shouldFindByUserName() {
        User user = new User();
        user.setUserName("findbyuser");
        user.setPassword("123456");
        user.setRole(2);
        userMapper.insert(user);

        User found = userMapper.findByUserName("findbyuser");

        assertNotNull(found, "应能通过用户名找到用户");
        assertEquals("findbyuser", found.getUserName());
    }

    @Test
    void shouldFindByPhone() {
        User user = new User();
        user.setUserName("phoneuser");
        user.setPassword("123456");
        user.setPhone("13800001111");
        user.setRole(2);
        userMapper.insert(user);

        User found = userMapper.findByPhone("13800001111");

        assertNotNull(found, "应能通过手机号找到用户");
        assertEquals("13800001111", found.getPhone());
    }

    @Test
    void shouldFindById() {
        User user = new User();
        user.setUserName("iduser");
        user.setPassword("123456");
        user.setRole(2);
        userMapper.insert(user);

        User found = userMapper.findById(user.getId());

        assertNotNull(found, "应能通过ID找到用户");
        assertEquals("iduser", found.getUserName());
    }

    @Test
    void shouldReturnNullForNonExistentUser() {
        User found = userMapper.findByUserName("no_such_user");
        assertNull(found, "不存在的用户应返回null");
    }
}
