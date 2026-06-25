package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.Cart;
import com.wardrobe.backend.entity.Clothes;
import com.wardrobe.backend.entity.Order;
import com.wardrobe.backend.entity.OrderItem;
import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.enums.OrderStatus;
import com.wardrobe.backend.exception.BusinessException;
import com.wardrobe.backend.exception.ForbiddenException;
import com.wardrobe.backend.mapper.CartMapper;
import com.wardrobe.backend.mapper.ClothesMapper;
import com.wardrobe.backend.mapper.OrderItemMapper;
import com.wardrobe.backend.mapper.OrderMapper;
import com.wardrobe.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务：结算下单
 */
@Service
public class CartService {

    private final CartMapper cartMapper;
    private final ClothesMapper clothesMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserMapper userMapper;

    public CartService(CartMapper cartMapper, ClothesMapper clothesMapper, OrderMapper orderMapper,
                       OrderItemMapper orderItemMapper, UserMapper userMapper) {
        this.cartMapper = cartMapper;
        this.clothesMapper = clothesMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.userMapper = userMapper;
    }

    // 结算：根据购物车 ID 列表生成订单（事务）
    @Transactional
    public Order checkout(List<Integer> ids, Integer userId) {
        // 加载选中的购物车条目
        List<Cart> items = ids.stream().map(cartMapper::findById).filter(c -> c != null).toList();
        if (items.isEmpty()) {
            throw new BusinessException(400, "未选择商品");
        }

        // 校验归属权
        for (Cart item : items) {
            if (!item.getUserId().equals(userId)) {
                throw new ForbiddenException("无权操作他人购物车");
            }
        }

        // 获取用户地址
        User user = userMapper.findById(userId);
        String address = user != null && user.getAddress() != null ? user.getAddress() : "";

        // 组装订单详情文本 + 计算总价 + 构建订单明细
        StringBuilder details = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart item : items) {
            Clothes c = clothesMapper.findById(item.getClothId());
            if (c == null) {
                throw new BusinessException(400, "商品 [" + item.getClothId() + "] 已下架，请先移除后再结算");
            }
            String sizeLabel = "均码".equals(item.getClothSize()) ? "" : "码";
            details.append("服装编号").append(c.getId()).append(", ")
                    .append(c.getClothName()).append(item.getClothSize()).append(sizeLabel).append(" (")
                    .append(c.getPrice()).append(") x").append(item.getAmount()).append("; ");
            total = total.add(c.getPrice().multiply(BigDecimal.valueOf(item.getAmount())));

            OrderItem oi = new OrderItem();
            oi.setClothId(c.getId());
            oi.setClothName(c.getClothName());
            oi.setClothSize(item.getClothSize());
            oi.setAmount(item.getAmount());
            oi.setPrice(c.getPrice());
            oi.setOperatorId(c.getOperatorId() != null ? c.getOperatorId() : 0);
            orderItems.add(oi);
        }

        // 创建订单（状态：未支付）
        Order order = new Order();
        order.setClothesDetails(details.toString());
        order.setPrice(total);
        order.setStatus(OrderStatus.UNPAID.getCode());
        order.setUserId(userId);
        order.setAddress(address);
        order.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        orderMapper.insert(order);

        // 批量插入订单明细
        for (OrderItem oi : orderItems) {
            oi.setOrderId(order.getId());
        }
        if (!orderItems.isEmpty()) {
            orderItemMapper.insertBatch(orderItems);
        }

        // 清空已结算的购物车条目
        cartMapper.deleteByIds(ids);

        return order;
    }
}
