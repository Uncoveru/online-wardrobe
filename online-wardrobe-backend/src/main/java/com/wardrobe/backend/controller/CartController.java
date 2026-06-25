package com.wardrobe.backend.controller;

import com.wardrobe.backend.dto.Result;
import com.wardrobe.backend.entity.Cart;
import com.wardrobe.backend.entity.Order;
import com.wardrobe.backend.exception.ForbiddenException;
import com.wardrobe.backend.mapper.CartMapper;
import com.wardrobe.backend.service.CartService;
import com.wardrobe.backend.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 购物车：增删改查 + 结算下单
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartMapper cartMapper;
    private final CartService cartService;

    public CartController(CartMapper cartMapper, CartService cartService) {
        this.cartMapper = cartMapper;
        this.cartService = cartService;
    }

    // 查询当前用户购物车列表
    @GetMapping
    public Result<List<Cart>> getCart(HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        return Result.ok(cartMapper.findByUserId(userId));
    }

    // 加入购物车（默认数量 1）
    @PostMapping
    public Result<Cart> addToCart(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        Cart cart = new Cart();
        cart.setClothId((Integer) body.get("clothId"));
        cart.setClothSize((String) body.get("clothSize"));
        cart.setAmount(body.containsKey("amount") ? (Integer) body.get("amount") : 1);
        cart.setUserId(userId);
        cart.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        cartMapper.insert(cart);
        return Result.ok(cart);
    }

    // 修改购物车条目数量（校验归属权）
    @PutMapping("/{id}")
    public Result<Void> updateAmount(@PathVariable Integer id, @RequestBody Map<String, Object> body,
                                     HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        Cart cart = cartMapper.findById(id);
        if (cart == null) {
            return Result.fail(404, "购物车条目不存在");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new ForbiddenException("无权操作他人购物车");
        }
        cartMapper.updateAmount(id, (Integer) body.get("amount"));
        return Result.ok();
    }

    // 删除购物车条目（校验归属权）
    @DeleteMapping("/{id}")
    public Result<Void> deleteItem(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserId(request);
        Cart cart = cartMapper.findById(id);
        if (cart == null) {
            return Result.fail(404, "购物车条目不存在");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new ForbiddenException("无权操作他人购物车");
        }
        cartMapper.deleteById(id);
        return Result.ok();
    }

    // 结算：将选中的购物车条目生成订单
    @PostMapping("/checkout")
    public Result<Order> checkout(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        Integer userId = AuthUtils.getUserId(request);
        Order order = cartService.checkout(ids, userId);
        return Result.ok(order);
    }
}
