package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.Order;
import com.wardrobe.backend.enums.OrderStatus;
import com.wardrobe.backend.exception.BusinessException;
import com.wardrobe.backend.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderMapper orderMapper;

    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public List<Order> getOrdersByParams(String userName, String status) {
        return orderMapper.findByParams(userName, status);
    }

    public List<Order> getAllOrders() {
        return orderMapper.findAll();
    }

    public void shipOrder(Integer id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.PAID.getCode().equals(order.getStatus())) {
            throw new BusinessException(400, "仅未发货的订单可发货");
        }
        orderMapper.updateStatus(id, OrderStatus.SHIPPED.getCode());
    }

    public void updateStatus(Integer id, String status) {
        orderMapper.updateStatus(id, status);
    }

    public List<Order> getUserOrders(Integer userId) {
        return orderMapper.findByUserId(userId);
    }
}
