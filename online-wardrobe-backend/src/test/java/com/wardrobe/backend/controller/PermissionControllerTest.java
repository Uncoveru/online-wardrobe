package com.wardrobe.backend.controller;

import com.wardrobe.backend.entity.Order;
import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.mapper.OrderMapper;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String operatorToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        User operator = new User();
        operator.setUserName("opuser");
        operator.setPassword(passwordEncoder.encode("testp1"));
        operator.setRole(3);
        operator.setStatus(1);
        userMapper.insert(operator);

        String opResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"opuser\",\"password\":\"testp1\"}"))
                .andReturn().getResponse().getContentAsString();
        operatorToken = "Bearer " + opResult.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");

        User regular = new User();
        regular.setUserName("reguser");
        regular.setPassword(passwordEncoder.encode("testp1"));
        regular.setRole(2);
        regular.setStatus(1);
        userMapper.insert(regular);

        String regResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"reguser\",\"password\":\"testp1\"}"))
                .andReturn().getResponse().getContentAsString();
        userToken = "Bearer " + regResult.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void operatorCanAddClothes() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

        mockMvc.perform(multipart("/api/clothes")
                        .file(file)
                        .param("clothName", "运营测试服装")
                        .param("typeId", "1")
                        .param("style", "时尚")
                        .param("price", "199.00")
                        .header("Authorization", operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void operatorCanManageOrders() throws Exception {
        Order order = new Order();
        order.setClothesDetails("运营订单测试");
        order.setPrice(new BigDecimal("100.00"));
        order.setStatus("1");
        order.setUserId(1);
        order.setAddress("测试");
        order.setTime("2024-07-22 10:00:00");
        orderMapper.insert(order);

        mockMvc.perform(put("/api/orders/" + order.getId() + "/ship")
                        .header("Authorization", operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void operatorCannotAccessUserManagement() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", operatorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void regularUserCannotAddClothes() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

        mockMvc.perform(multipart("/api/clothes")
                        .file(file)
                        .param("clothName", "普通用户测试")
                        .param("typeId", "1")
                        .param("style", "时尚")
                        .param("price", "99.00")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void regularUserCannotManageOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }
}
