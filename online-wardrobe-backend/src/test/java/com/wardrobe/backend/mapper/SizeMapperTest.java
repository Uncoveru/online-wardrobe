package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Size;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SizeMapperTest {

    @Autowired
    private SizeMapper sizeMapper;

    @Test
    void shouldFindSizesByTypeId() {
        List<Size> sizes = sizeMapper.findByTypeId(1);
        assertNotNull(sizes);
        assertEquals(6, sizes.size());
        assertEquals("S", sizes.get(0).getSizeName());
    }

    @Test
    void shouldReturnEmptyForInvalidTypeId() {
        List<Size> sizes = sizeMapper.findByTypeId(999);
        assertNotNull(sizes);
        assertTrue(sizes.isEmpty());
    }
}
