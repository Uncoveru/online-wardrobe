package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.Clothes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClothesServiceTest {

    @Autowired
    private ClothesService clothesService;

    @TempDir
    Path tempDir;

    @Test
    void shouldGetAllClothes() {
        Clothes c = new Clothes();
        c.setClothName("测试服装");
        c.setTypeId(1);
        c.setStyle("时尚");
        c.setPrice(new BigDecimal("199.00"));
        clothesService.addClothes(c, null, null);

        List<Clothes> list = clothesService.getClothesList(null);
        assertNotNull(list);
        assertTrue(list.size() >= 1);
    }

    @Test
    void shouldGetClothesByParams() {
        Clothes c1 = new Clothes();
        c1.setClothName("红色裙子");
        c1.setTypeId(1);
        c1.setStyle("时尚");
        c1.setPrice(new BigDecimal("299.00"));
        clothesService.addClothes(c1, null, null);

        List<Clothes> result = clothesService.getClothesByParams("红色", null, null, null);
        assertEquals(1, result.size());
        assertEquals("红色裙子", result.get(0).getClothName());
    }

    @Test
    void shouldAddClothesWithImage() {
        Clothes c = new Clothes();
        c.setClothName("新品上架");
        c.setTypeId(2);
        c.setStyle("休闲");
        c.setPrice(new BigDecimal("199.00"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "test image".getBytes()
        );

        Clothes saved = clothesService.addClothes(c, file, null);

        assertNotNull(saved.getId());
        assertNotNull(saved.getImage());
        assertTrue(saved.getImage().endsWith(".png"));
    }

    @Test
    void shouldUpdateClothes() {
        Clothes c = new Clothes();
        c.setClothName("旧名称");
        c.setTypeId(1);
        c.setStyle("休闲");
        c.setPrice(new BigDecimal("100.00"));
        clothesService.addClothes(c, null, null);

        c.setClothName("更新名称");
        c.setPrice(new BigDecimal("150.00"));
        clothesService.updateClothes(c, null, null);

        Clothes updated = clothesService.getClothesById(c.getId());
        assertEquals("更新名称", updated.getClothName());
        assertEquals(new BigDecimal("150.00"), updated.getPrice());
    }

    @Test
    void shouldDeleteClothes() {
        Clothes c = new Clothes();
        c.setClothName("待删除");
        c.setTypeId(1);
        c.setStyle("休闲");
        c.setPrice(new BigDecimal("50.00"));
        clothesService.addClothes(c, null, null);

        clothesService.deleteClothes(c.getId(), null);

        Clothes found = clothesService.getClothesById(c.getId());
        assertNull(found);
    }
}
