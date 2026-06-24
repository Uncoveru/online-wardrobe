package com.wardrobe.backend.controller;

import com.wardrobe.backend.entity.Clothes;
import com.wardrobe.backend.mapper.ClothesMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClothesControllerAdditionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClothesMapper clothesMapper;

    @Test
    void shouldGetClothesById() throws Exception {
        Clothes c = new Clothes();
        c.setClothName("详情测试");
        c.setTypeId(1);
        c.setStyle("时尚");
        c.setPrice(new BigDecimal("199.00"));
        clothesMapper.insert(c);

        mockMvc.perform(get("/api/clothes/" + c.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.clothName").value("详情测试"));
    }

    @Test
    void shouldReturnErrorForNonExistentClothes() throws Exception {
        mockMvc.perform(get("/api/clothes/9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
