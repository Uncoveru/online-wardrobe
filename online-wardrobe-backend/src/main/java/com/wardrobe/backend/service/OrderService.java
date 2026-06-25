package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.Order;
import com.wardrobe.backend.entity.OrderItem;
import com.wardrobe.backend.enums.OrderStatus;
import com.wardrobe.backend.exception.BusinessException;
import com.wardrobe.backend.mapper.OrderItemMapper;
import com.wardrobe.backend.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    public List<Order> getOrdersByParams(String userName, String status) {
        List<Order> orders = orderMapper.findByParams(userName, status);
        populateOrderItems(orders);
        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderMapper.findAll();
        populateOrderItems(orders);
        return orders;
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
        List<Order> orders = orderMapper.findByUserId(userId);
        populateOrderItems(orders);
        return orders;
    }

    public List<Order> getOrdersByOperatorId(Integer operatorId) {
        List<Order> orders = orderMapper.findByOperatorId(operatorId);
        if (orders.isEmpty()) {
            return orders;
        }
        List<Integer> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        List<OrderItem> allItems = orderItemMapper.findByOrderIds(orderIds);
        Map<Integer, List<OrderItem>> itemsByOrder = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        for (Order order : orders) {
            List<OrderItem> items = itemsByOrder.getOrDefault(order.getId(), Collections.emptyList());
            List<OrderItem> filtered = items.stream()
                    .filter(i -> operatorId.equals(i.getOperatorId()))
                    .collect(Collectors.toList());
            order.setOrderItems(filtered);
        }
        return orders;
    }

    private void populateOrderItems(List<Order> orders) {
        if (orders.isEmpty()) return;
        List<Integer> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        List<OrderItem> allItems = orderItemMapper.findByOrderIds(orderIds);
        Map<Integer, List<OrderItem>> itemsByOrder = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        for (Order order : orders) {
            order.setOrderItems(itemsByOrder.getOrDefault(order.getId(), Collections.emptyList()));
        }
    }
}
