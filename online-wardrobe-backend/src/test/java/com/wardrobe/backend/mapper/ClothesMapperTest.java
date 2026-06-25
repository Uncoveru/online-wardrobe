package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Clothes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClothesMapperTest {

    @Autowired
    private ClothesMapper clothesMapper;

    @Test
    void shouldInsertAndFindById() {
        Clothes c = new Clothes();
        c.setClothName("测试连衣裙");
        c.setImage("test.jpg");
        c.setTypeId(1);
        c.setStyle("时尚");
        c.setPrice(new BigDecimal("299.90"));

        clothesMapper.insert(c);
        assertNotNull(c.getId());

        Clothes found = clothesMapper.findById(c.getId());
        assertNotNull(found);
        assertEquals("测试连衣裙", found.getClothName());
        assertEquals(new BigDecimal("299.90"), found.getPrice());
    }

    @Test
    void shouldFindAll() {
        Clothes c1 = new Clothes();
        c1.setClothName("衬衫");
        c1.setTypeId(1);
        c1.setStyle("休闲");
        c1.setPrice(new BigDecimal("199.00"));
        clothesMapper.insert(c1);

        Clothes c2 = new Clothes();
        c2.setClothName("牛仔裤");
        c2.setTypeId(2);
        c2.setStyle("休闲");
        c2.setPrice(new BigDecimal("399.00"));
        clothesMapper.insert(c2);

        List<Clothes> list = clothesMapper.findAll(null);
        assertNotNull(list);
        assertTrue(list.size() >= 2);
    }

    @Test
    void shouldFindByParams() {
        Clothes c1 = new Clothes();
        c1.setClothName("时尚衬衫");
        c1.setTypeId(1);
        c1.setStyle("时尚");
        c1.setPrice(new BigDecimal("259.00"));
        clothesMapper.insert(c1);

        Clothes c2 = new Clothes();
        c2.setClothName("商务衬衫");
        c2.setTypeId(1);
        c2.setStyle("商务测试专用");
        c2.setPrice(new BigDecimal("359.00"));
        clothesMapper.insert(c2);

        List<Clothes> byName = clothesMapper.findByParams("时尚", null, null, null);
        assertEquals(1, byName.size());
        assertEquals("时尚衬衫", byName.get(0).getClothName());

        List<Clothes> byStyle = clothesMapper.findByParams(null, "商务测试专用", null, null);
        assertEquals(1, byStyle.size());
        assertEquals("商务衬衫", byStyle.get(0).getClothName());

        List<Clothes> byType = clothesMapper.findByParams(null, null, 1, null);
        assertTrue(byType.size() >= 2);
    }

    @Test
    void shouldUpdateClothes() {
        Clothes c = new Clothes();
        c.setClothName("旧名称");
        c.setTypeId(1);
        c.setStyle("休闲");
        c.setPrice(new BigDecimal("100.00"));
        clothesMapper.insert(c);

        c.setClothName("新名称");
        c.setPrice(new BigDecimal("150.00"));
        clothesMapper.update(c);

        Clothes updated = clothesMapper.findById(c.getId());
        assertEquals("新名称", updated.getClothName());
        assertEquals(new BigDecimal("150.00"), updated.getPrice());
    }

    @Test
    void shouldDeleteClothes() {
        Clothes c = new Clothes();
        c.setClothName("待删除");
        c.setTypeId(1);
        c.setStyle("休闲");
        c.setPrice(new BigDecimal("50.00"));
        clothesMapper.insert(c);

        clothesMapper.deleteById(c.getId());

        Clothes found = clothesMapper.findById(c.getId());
        assertNull(found);
    }
}
