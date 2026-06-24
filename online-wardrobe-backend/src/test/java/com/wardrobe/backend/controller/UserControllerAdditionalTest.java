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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerAdditionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String authHeader;

    @BeforeEach
    void login() throws Exception {
        User user = new User();
        user.setUserName("profiletest");
        user.setPassword(passwordEncoder.encode("oldpw"));
        user.setPhone("13900000001");
        user.setAddress("旧地址");
        user.setRole(2);
        userMapper.insert(user);

        String result = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"profiletest\",\"password\":\"oldpw\"}"))
                .andReturn().getResponse().getContentAsString();

        String token = result.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        authHeader = "Bearer " + token;
    }

    @Test
    void shouldUpdateProfile() throws Exception {
        mockMvc.perform(put("/api/user/profile")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"13811112222\",\"address\":\"新地址\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldUpdatePassword() throws Exception {
        mockMvc.perform(put("/api/user/password")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldpw\",\"newPassword\":\"newpw1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
