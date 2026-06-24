package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void shouldInsertAndFindById() {
        Order order = new Order();
        order.setClothesDetails("服装编号1, 连衣裙S码 (299.90) x2");
        order.setPrice(new BigDecimal("599.80"));
        order.setStatus("1");
        order.setUserId(1);
        order.setAddress("北京市昌平区");
        order.setTime("2024-07-22 08:51:41");

        orderMapper.insert(order);
        assertNotNull(order.getId());

        Order found = orderMapper.findById(order.getId());
        assertNotNull(found);
        assertEquals("1", found.getStatus());
        assertEquals(Integer.valueOf(1), found.getUserId());
    }

    @Test
    void shouldFindAll() {
        Order o1 = new Order();
        o1.setClothesDetails("商品A");
        o1.setPrice(new BigDecimal("100.00"));
        o1.setStatus("1");
        o1.setUserId(1);
        o1.setAddress("地址A");
        o1.setTime("2024-01-01 10:00:00");
        orderMapper.insert(o1);

        Order o2 = new Order();
        o2.setClothesDetails("商品B");
        o2.setPrice(new BigDecimal("200.00"));
        o2.setStatus("2");
        o2.setUserId(1);
        o2.setAddress("地址B");
        o2.setTime("2024-01-02 10:00:00");
        orderMapper.insert(o2);

        List<Order> list = orderMapper.findAll();
        assertTrue(list.size() >= 2);
    }

    @Test
    void shouldFindByParams() {
        Order o1 = new Order();
        o1.setClothesDetails("订单搜索测试1");
        o1.setPrice(new BigDecimal("100.00"));
        o1.setStatus("1");
        o1.setUserId(1);
        o1.setAddress("北京");
        o1.setTime("2024-07-01 10:00:00");
        orderMapper.insert(o1);

        Order o2 = new Order();
        o2.setClothesDetails("订单搜索测试2");
        o2.setPrice(new BigDecimal("200.00"));
        o2.setStatus("2");
        o2.setUserId(1);
        o2.setAddress("上海");
        o2.setTime("2024-08-01 10:00:00");
        orderMapper.insert(o2);

        List<Order> byStatus = orderMapper.findByParams(null, "1");
        assertEquals(1, byStatus.size());
        assertEquals("1", byStatus.get(0).getStatus());

        List<Order> byUserName = orderMapper.findByParams("admin", null);
        assertTrue(byUserName.size() >= 2);
    }

    @Test
    void shouldUpdateStatus() {
        Order order = new Order();
        order.setClothesDetails("待发货订单");
        order.setPrice(new BigDecimal("300.00"));
        order.setStatus("1");
        order.setUserId(1);
        order.setAddress("测试地址");
        order.setTime("2024-07-22 10:00:00");
        orderMapper.insert(order);

        orderMapper.updateStatus(order.getId(), "2");

        Order updated = orderMapper.findById(order.getId());
        assertEquals("2", updated.getStatus());
    }
}
