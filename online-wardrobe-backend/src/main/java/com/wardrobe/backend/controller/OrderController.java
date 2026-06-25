package com.wardrobe.backend.controller;

import com.wardrobe.backend.dto.Result;
import com.wardrobe.backend.entity.Order;
import com.wardrobe.backend.enums.OrderStatus;
import com.wardrobe.backend.enums.RolePermission;
import com.wardrobe.backend.exception.ForbiddenException;
import com.wardrobe.backend.mapper.OrderMapper;
import com.wardrobe.backend.service.OrderService;
import com.wardrobe.backend.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/orders")
    public Result<List<Order>> listOrders(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.ORDERS_MANAGE);
        Integer role = AuthUtils.getRole(request);
        if (role == 3) {
            Integer operatorId = AuthUtils.getUserId(request);
            return Result.ok(orderService.getOrdersByOperatorId(operatorId));
        }
        if (userName == null && status == null) {
            return Result.ok(orderService.getAllOrders());
        }
        return Result.ok(orderService.getOrdersByParams(userName, status));
    }

    @PutMapping("/orders/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Integer id, HttpServletRequest request) {
        requirePermission(request, RolePermission.Permission.ORDERS_MANAGE);
        orderService.shipOrder(id);
        return Result.ok();
    }

    @GetMapping("/user/orders")
    public Result<List<Order>> userOrders(HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        return Result.ok(orderService.getUserOrders(userId));
    }

    @PutMapping("/user/orders/{id}/pay")
    public Result<Void> payOrder(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        Order order = orderMapper.findById(id);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("无权操作他人订单");
        }
        if (!OrderStatus.UNPAID.getCode().equals(order.getStatus())) {
            return Result.fail(400, "该订单已支付，请勿重复操作");
        }
        orderService.updateStatus(id, OrderStatus.PAID.getCode());
        return Result.ok();
    }

    @PutMapping("/user/orders/{id}/confirm")
    public Result<Void> confirmReceived(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        Order order = orderMapper.findById(id);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("无权操作他人订单");
        }
        if (!OrderStatus.SHIPPED.getCode().equals(order.getStatus())) {
            return Result.fail(400, "仅已发货的订单可确认收货");
        }
        orderService.updateStatus(id, OrderStatus.RECEIVED.getCode());
        return Result.ok();
    }

    private void requirePermission(HttpServletRequest request, String permission) {
        Integer role = AuthUtils.getRole(request);
        if (!RolePermission.fromId(role).hasPermission(permission)) {
            throw new ForbiddenException("无权限，仅管理员可操作");
        }
    }
}
