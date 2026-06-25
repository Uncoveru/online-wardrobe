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

/**
 * 订单服务：查询 / 状态变更 / 订单明细填充
 */
@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    // 按用户名/状态筛选订单
    public List<Order> getOrdersByParams(String userName, String status) {
        List<Order> orders = orderMapper.findByParams(userName, status);
        populateOrderItems(orders);
        return orders;
    }

    // 全部订单
    public List<Order> getAllOrders() {
        List<Order> orders = orderMapper.findAll();
        populateOrderItems(orders);
        return orders;
    }

    // 发货（状态：未发货 → 已发货）
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

    // 更新订单状态
    public void updateStatus(Integer id, String status) {
        orderMapper.updateStatus(id, status);
    }

    // 用户端：查询自己的订单
    public List<Order> getUserOrders(Integer userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        populateOrderItems(orders);
        return orders;
    }

    // 操作员：查看与自己商品相关的订单（仅展示自己的商品明细）
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
            // 过滤：只保留该操作员自己的商品明细
            List<OrderItem> filtered = items.stream()
                    .filter(i -> operatorId.equals(i.getOperatorId()))
                    .collect(Collectors.toList());
            order.setOrderItems(filtered);
        }
        return orders;
    }

    // 批量填充订单的明细列表
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
