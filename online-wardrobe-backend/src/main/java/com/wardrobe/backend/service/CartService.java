package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.Cart;
import com.wardrobe.backend.entity.Clothes;
import com.wardrobe.backend.entity.Order;
import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.enums.OrderStatus;
import com.wardrobe.backend.exception.BusinessException;
import com.wardrobe.backend.exception.ForbiddenException;
import com.wardrobe.backend.mapper.CartMapper;
import com.wardrobe.backend.mapper.ClothesMapper;
import com.wardrobe.backend.mapper.OrderMapper;
import com.wardrobe.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CartService {

    private final CartMapper cartMapper;
    private final ClothesMapper clothesMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public CartService(CartMapper cartMapper, ClothesMapper clothesMapper, OrderMapper orderMapper, UserMapper userMapper) {
        this.cartMapper = cartMapper;
        this.clothesMapper = clothesMapper;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public Order checkout(List<Integer> ids, Integer userId) {
        List<Cart> items = ids.stream().map(cartMapper::findById).filter(c -> c != null).toList();
        if (items.isEmpty()) {
            throw new BusinessException(400, "未选择商品");
        }

        for (Cart item : items) {
            if (!item.getUserId().equals(userId)) {
                throw new ForbiddenException("无权操作他人购物车");
            }
        }

        User user = userMapper.findById(userId);
        String address = user != null && user.getAddress() != null ? user.getAddress() : "";

        StringBuilder details = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;
        for (Cart item : items) {
            Clothes c = clothesMapper.findById(item.getClothId());
            if (c == null) {
                throw new BusinessException(400, "商品 [" + item.getClothId() + "] 已下架，请先移除后再结算");
            }
            details.append("服装编号").append(c.getId()).append(", ")
                    .append(c.getClothName()).append(item.getClothSize()).append("码 (")
                    .append(c.getPrice()).append(") x").append(item.getAmount()).append("; ");
            total = total.add(c.getPrice().multiply(BigDecimal.valueOf(item.getAmount())));
        }

        Order order = new Order();
        order.setClothesDetails(details.toString());
        order.setPrice(total);
        order.setStatus(OrderStatus.UNPAID.getCode());
        order.setUserId(userId);
        order.setAddress(address);
        order.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        orderMapper.insert(order);
        cartMapper.deleteByIds(ids);

        return order;
    }
}
