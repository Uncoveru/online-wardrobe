package com.wardrobe.backend.controller;

import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String adminToken;
    private Integer adminUserId;

    @BeforeEach
    void setUp() throws Exception {
        User admin = new User();
        admin.setUserName("admintest");
        admin.setPassword(passwordEncoder.encode("adminp1"));
        admin.setRole(1);
        admin.setStatus(1);
        userMapper.insert(admin);
        adminUserId = admin.getId();

        String result = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"admintest\",\"password\":\"adminp1\"}"))
                .andReturn().getResponse().getContentAsString();
        adminToken = "Bearer " + result.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void shouldNotDeleteSelf() throws Exception {
        mockMvc.perform(delete("/api/admin/users/" + adminUserId)
                        .header("Authorization", adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteOtherUser() throws Exception {
        User other = new User();
        other.setUserName("otheruser");
        other.setPassword(passwordEncoder.encode("otherp1"));
        other.setRole(2);
        other.setStatus(1);
        userMapper.insert(other);

        mockMvc.perform(delete("/api/admin/users/" + other.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldMaskPhoneInUserList() throws Exception {
        User user = new User();
        user.setUserName("phoneuser");
        user.setPassword(passwordEncoder.encode("phonet1"));
        user.setPhone("13812345678");
        user.setRole(2);
        user.setStatus(1);
        userMapper.insert(user);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[?(@.userName=='phoneuser')].phone")
                        .value("13812345678"));
    }

    @Test
    void shouldNotReturnPasswordInUserList() throws Exception {
        User user = new User();
        user.setUserName("nopassuser");
        user.setPassword(passwordEncoder.encode("haspass1"));
        user.setRole(2);
        user.setStatus(1);
        userMapper.insert(user);

        String body = mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("\"userName\":\"nopassuser\""));
        assertTrue(body.contains("\"password\":null"));
    }

    @Test
    void softDeletedUserShouldNotLogin() throws Exception {
        User user = new User();
        user.setUserName("softdel");
        user.setPassword(passwordEncoder.encode("softd1"));
        user.setRole(2);
        user.setStatus(1);
        userMapper.insert(user);

        mockMvc.perform(delete("/api/admin/users/" + user.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());

        String loginResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"softdel\",\"password\":\"softd1\"}"))
                .andReturn().getResponse().getContentAsString();
        org.hamcrest.MatcherAssert.assertThat(loginResult,
                org.hamcrest.Matchers.containsString("用户名或密码错误"));
    }

    @Test
    void softDeletedUserShouldNotAppearInList() throws Exception {
        User user = new User();
        user.setUserName("hiddenuser");
        user.setPassword(passwordEncoder.encode("hidden1"));
        user.setRole(2);
        user.setStatus(1);
        userMapper.insert(user);

        mockMvc.perform(delete("/api/admin/users/" + user.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());

        String listResult = mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", adminToken))
                .andReturn().getResponse().getContentAsString();
        org.hamcrest.MatcherAssert.assertThat(listResult,
                org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("hiddenuser")));
    }
}
