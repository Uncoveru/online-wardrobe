package com.wardrobe.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        String json = """
                {
                    "userName": "controlleruser",
                    "password": "test123",
                    "phone": "13500000000",
                    "address": "广州市"
                }""";

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user.userName").value("controlleruser"))
                .andExpect(jsonPath("$.data.user.role").value(2))
                .andExpect(jsonPath("$.data.token").isString());
    }

    @Test
    void shouldRejectDuplicateRegistration() throws Exception {
        String json = """
                {
                    "userName": "duplicatectrl",
                    "password": "test123",
                    "phone": "13400000000",
                    "address": "深圳市"
                }""";

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void shouldLoginWithUsername() throws Exception {
        String registerJson = """
                {
                    "userName": "loginuser",
                    "password": "logint1",
                    "phone": "13300000000",
                    "address": "杭州市"
                }""";

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = """
                {
                    "account": "loginuser",
                    "password": "logint1"
                }""";

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user.userName").value("loginuser"))
                .andExpect(jsonPath("$.data.token").isString());
    }

    @Test
    void shouldRejectWrongPasswordLogin() throws Exception {
        String registerJson = """
                {
                    "userName": "wrongpw",
                    "password": "rightp1",
                    "phone": "13200000000",
                    "address": "南京市"
                }""";

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = """
                {
                    "account": "wrongpw",
                    "password": "wrongp1"
                }""";

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldLoginWithPhone() throws Exception {
        String registerJson = """
                {
                    "userName": "phonelogin",
                    "password": "phone1",
                    "phone": "13100000000",
                    "address": "成都市"
                }""";

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = """
                {
                    "account": "13100000000",
                    "password": "phone1"
                }""";

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user.userName").value("phonelogin"))
                .andExpect(jsonPath("$.data.token").isString());
    }
}
