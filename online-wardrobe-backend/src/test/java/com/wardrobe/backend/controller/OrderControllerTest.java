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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String authHeader;
    private Integer testUserId;

    @BeforeEach
    void login() throws Exception {
        User user = new User();
        user.setUserName("orderadmin");
        user.setPassword(passwordEncoder.encode("testp1"));
        user.setRole(1);
        user.setStatus(1);
        userMapper.insert(user);
        testUserId = user.getId();

        String result = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"orderadmin\",\"password\":\"testp1\"}"))
                .andReturn().getResponse().getContentAsString();

        String token = result.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        authHeader = "Bearer " + token;
    }

    @Test
    void shouldListOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldSearchOrdersByStatus() throws Exception {
        Order order = new Order();
        order.setClothesDetails("测试订单");
        order.setPrice(new BigDecimal("100.00"));
        order.setStatus("1");
        order.setUserId(testUserId);
        order.setAddress("测试");
        order.setTime("2024-07-22 10:00:00");
        orderMapper.insert(order);

        mockMvc.perform(get("/api/orders")
                        .header("Authorization", authHeader)
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].status").value("1"));
    }

    @Test
    void shouldSearchOrdersByUserName() throws Exception {
        Order order = new Order();
        order.setClothesDetails("用户名搜索订单");
        order.setPrice(new BigDecimal("200.00"));
        order.setStatus("1");
        order.setUserId(testUserId);
        order.setAddress("测试");
        order.setTime("2024-07-22 10:00:00");
        orderMapper.insert(order);

        mockMvc.perform(get("/api/orders")
                        .header("Authorization", authHeader)
                        .param("userName", "orderadmin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldShipOrder() throws Exception {
        Order order = new Order();
        order.setClothesDetails("待发货");
        order.setPrice(new BigDecimal("300.00"));
        order.setStatus("1");
        order.setUserId(testUserId);
        order.setAddress("测试");
        order.setTime("2024-07-22 10:00:00");
        orderMapper.insert(order);

        mockMvc.perform(put("/api/orders/" + order.getId() + "/ship")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
