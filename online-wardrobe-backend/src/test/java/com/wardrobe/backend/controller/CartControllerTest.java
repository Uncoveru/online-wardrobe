package com.wardrobe.backend.controller;

import com.wardrobe.backend.entity.Clothes;
import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.mapper.ClothesMapper;
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
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClothesMapper clothesMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String authHeader;

    @BeforeEach
    void login() throws Exception {
        User user = new User();
        user.setUserName("carttestuser");
        user.setPassword(passwordEncoder.encode("testpw"));
        user.setRole(2);
        user.setStatus(1);
        userMapper.insert(user);

        String loginResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"carttestuser\",\"password\":\"testpw\"}"))
                .andReturn().getResponse().getContentAsString();

        String token = loginResult.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        authHeader = "Bearer " + token;
    }

    @Test
    void shouldAddToCart() throws Exception {
        Clothes c = new Clothes();
        c.setClothName("购物车测试商品");
        c.setTypeId(1);
        c.setStyle("时尚");
        c.setPrice(new BigDecimal("199.00"));
        clothesMapper.insert(c);

        String json = String.format("{\"clothId\":%d,\"clothSize\":\"M\",\"amount\":1}", c.getId());

        mockMvc.perform(post("/api/cart")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.clothSize").value("M"));
    }

    @Test
    void shouldListCart() throws Exception {
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldUpdateCartAmount() throws Exception {
        Clothes c = new Clothes();
        c.setClothName("更新数量测试");
        c.setTypeId(1);
        c.setStyle("休闲");
        c.setPrice(new BigDecimal("99.00"));
        clothesMapper.insert(c);

        String addJson = String.format("{\"clothId\":%d,\"clothSize\":\"L\",\"amount\":1}", c.getId());
        String addResult = mockMvc.perform(post("/api/cart")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addJson))
                .andReturn().getResponse().getContentAsString();

        int cartId = Integer.parseInt(
                addResult.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(put("/api/cart/" + cartId)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldDeleteCartItem() throws Exception {
        Clothes c = new Clothes();
        c.setClothName("删除购物车测试");
        c.setTypeId(1);
        c.setStyle("时尚");
        c.setPrice(new BigDecimal("50.00"));
        clothesMapper.insert(c);

        String addJson = String.format("{\"clothId\":%d,\"clothSize\":\"S\",\"amount\":1}", c.getId());
        String addResult = mockMvc.perform(post("/api/cart")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addJson))
                .andReturn().getResponse().getContentAsString();

        int cartId = Integer.parseInt(
                addResult.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(delete("/api/cart/" + cartId)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldCheckout() throws Exception {
        Clothes c = new Clothes();
        c.setClothName("结算测试商品");
        c.setTypeId(1);
        c.setStyle("时尚");
        c.setPrice(new BigDecimal("299.00"));
        clothesMapper.insert(c);

        String addJson = String.format("{\"clothId\":%d,\"clothSize\":\"M\",\"amount\":2}", c.getId());
        String addResult = mockMvc.perform(post("/api/cart")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addJson))
                .andReturn().getResponse().getContentAsString();

        int cartId = Integer.parseInt(
                addResult.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(post("/api/cart/checkout")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"ids\":[%d]}", cartId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
