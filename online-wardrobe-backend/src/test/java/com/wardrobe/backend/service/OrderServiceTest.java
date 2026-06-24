package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.Order;
import com.wardrobe.backend.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void shouldUpdateStatusToShipped() {
        Order order = new Order();
        order.setClothesDetails("测试");
        order.setPrice(new BigDecimal("100.00"));
        order.setStatus("1");
        order.setUserId(1);
        order.setAddress("测试地址");
        order.setTime("2024-07-22 10:00:00");
        orderMapper.insert(order);

        orderService.updateStatus(order.getId(), "2");

        Order updated = orderMapper.findById(order.getId());
        assertEquals("2", updated.getStatus());
    }

    @Test
    void shouldUpdateStatusToDelivered() {
        Order order = new Order();
        order.setClothesDetails("测试");
        order.setPrice(new BigDecimal("100.00"));
        order.setStatus("2");
        order.setUserId(1);
        order.setAddress("测试地址");
        order.setTime("2024-07-22 10:00:00");
        orderMapper.insert(order);

        orderService.updateStatus(order.getId(), "3");

        Order updated = orderMapper.findById(order.getId());
        assertEquals("3", updated.getStatus());
    }

    @Test
    void shouldShipOrder() {
        Order order = new Order();
        order.setClothesDetails("待发货");
        order.setPrice(new BigDecimal("100.00"));
        order.setStatus("1");
        order.setUserId(1);
        order.setAddress("测试");
        order.setTime("2024-07-22 10:00:00");
        orderMapper.insert(order);

        orderService.shipOrder(order.getId());

        Order updated = orderMapper.findById(order.getId());
        assertEquals("2", updated.getStatus());
    }
}
