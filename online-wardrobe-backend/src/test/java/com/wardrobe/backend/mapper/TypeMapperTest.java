package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TypeMapperTest {

    @Autowired
    private TypeMapper typeMapper;

    @Test
    void shouldFindAllTypes() {
        List<Type> types = typeMapper.findAll();
        assertNotNull(types);
        assertTrue(types.size() >= 4, "至少应有4个初始类型");
        assertEquals("衣服", types.get(0).getTypeName());
    }
}
