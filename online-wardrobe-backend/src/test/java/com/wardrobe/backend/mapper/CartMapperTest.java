package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Cart;
import com.wardrobe.backend.entity.Clothes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartMapperTest {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ClothesMapper clothesMapper;

    private Clothes testClothes;

    @BeforeEach
    void setUp() {
        testClothes = new Clothes();
        testClothes.setClothName("Cart测试服装");
        testClothes.setTypeId(1);
        testClothes.setStyle("时尚");
        testClothes.setPrice(new BigDecimal("100.00"));
        clothesMapper.insert(testClothes);
    }

    @Test
    void shouldInsertAndFindById() {
        Cart cart = new Cart();
        cart.setClothId(testClothes.getId());
        cart.setClothSize("M");
        cart.setAmount(2);
        cart.setUserId(1);
        cart.setDate("2024-07-22 10:00:00");

        cartMapper.insert(cart);
        assertNotNull(cart.getId());

        Cart found = cartMapper.findById(cart.getId());
        assertNotNull(found);
        assertEquals("M", found.getClothSize());
        assertEquals(2, found.getAmount());
    }

    @Test
    void shouldFindByUserId() {
        Cart c1 = new Cart();
        c1.setClothId(testClothes.getId());
        c1.setClothSize("S");
        c1.setAmount(1);
        c1.setUserId(1);
        c1.setDate("2024-07-22 10:00:00");
        cartMapper.insert(c1);

        Cart c2 = new Cart();
        c2.setClothId(testClothes.getId());
        c2.setClothSize("L");
        c2.setAmount(3);
        c2.setUserId(1);
        c2.setDate("2024-07-22 10:00:00");
        cartMapper.insert(c2);

        List<Cart> items = cartMapper.findByUserId(1);
        assertEquals(2, items.size());
    }

    @Test
    void shouldUpdateAmount() {
        Cart cart = new Cart();
        cart.setClothId(testClothes.getId());
        cart.setClothSize("M");
        cart.setAmount(2);
        cart.setUserId(1);
        cart.setDate("2024-07-22 10:00:00");
        cartMapper.insert(cart);

        cartMapper.updateAmount(cart.getId(), 5);
        Cart updated = cartMapper.findById(cart.getId());
        assertEquals(5, updated.getAmount());
    }

    @Test
    void shouldDeleteById() {
        Cart cart = new Cart();
        cart.setClothId(testClothes.getId());
        cart.setClothSize("M");
        cart.setAmount(1);
        cart.setUserId(1);
        cart.setDate("2024-07-22 10:00:00");
        cartMapper.insert(cart);

        cartMapper.deleteById(cart.getId());
        assertNull(cartMapper.findById(cart.getId()));
    }

    @Test
    void shouldDeleteByIds() {
        Cart c1 = new Cart();
        c1.setClothId(testClothes.getId()); c1.setClothSize("S"); c1.setAmount(1); c1.setUserId(1); c1.setDate("now");
        cartMapper.insert(c1);
        Cart c2 = new Cart();
        c2.setClothId(testClothes.getId()); c2.setClothSize("M"); c2.setAmount(1); c2.setUserId(1); c2.setDate("now");
        cartMapper.insert(c2);

        cartMapper.deleteByIds(List.of(c1.getId(), c2.getId()));

        assertNull(cartMapper.findById(c1.getId()));
        assertNull(cartMapper.findById(c2.getId()));
    }
}
