package com.wardrobe.backend.controller;

import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClothesControllerTest {

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
        user.setUserName("clothesadmin");
        user.setPassword(passwordEncoder.encode("adminp1"));
        user.setRole(1);
        userMapper.insert(user);

        String result = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"clothesadmin\",\"password\":\"adminp1\"}"))
                .andReturn().getResponse().getContentAsString();

        String token = result.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        authHeader = "Bearer " + token;
    }

    @Test
    void shouldListAllClothes() throws Exception {
        mockMvc.perform(get("/api/clothes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldAddClothes() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "test".getBytes()
        );

        mockMvc.perform(multipart("/api/clothes")
                        .file(file)
                        .param("clothName", "API测试服装")
                        .param("typeId", "1")
                        .param("style", "时尚")
                        .param("price", "299.90")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.clothName").value("API测试服装"))
                .andExpect(jsonPath("$.data.image").isNotEmpty());
    }

    @Test
    void shouldSearchClothes() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]
        );

        mockMvc.perform(multipart("/api/clothes")
                        .file(file)
                        .param("clothName", "搜索测试")
                        .param("typeId", "2")
                        .param("style", "商务")
                        .param("price", "399.00")
                        .header("Authorization", authHeader));

        mockMvc.perform(get("/api/clothes/search")
                        .param("clothName", "搜索"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].clothName").value("搜索测试"));
    }

    @Test
    void shouldUpdateClothes() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]
        );

        mockMvc.perform(multipart("/api/clothes")
                .file(file)
                .param("clothName", "待更新")
                .param("typeId", "1")
                .param("style", "休闲")
                .param("price", "100.00")
                .header("Authorization", authHeader));

        mockMvc.perform(multipart("/api/clothes/1")
                        .file(new MockMultipartFile("file", "", "image/png", new byte[0]))
                        .param("clothName", "已更新")
                        .param("typeId", "1")
                        .param("style", "时尚")
                        .param("price", "150.00")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.clothName").value("已更新"));
    }

    @Test
    void shouldDeleteClothes() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]
        );

        mockMvc.perform(multipart("/api/clothes")
                .file(file)
                .param("clothName", "待删除")
                .param("typeId", "1")
                .param("style", "休闲")
                .param("price", "50.00")
                .header("Authorization", authHeader));

        mockMvc.perform(delete("/api/clothes/1")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldListTypes() throws Exception {
        mockMvc.perform(get("/api/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].typeName").value("衣服"));
    }

    @Test
    void shouldListSizesByType() throws Exception {
        mockMvc.perform(get("/api/sizes").param("typeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(4));
    }
}
