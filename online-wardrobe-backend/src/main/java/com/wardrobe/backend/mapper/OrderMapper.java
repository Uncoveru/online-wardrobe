package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper {

    // 创建订单
    void insert(Order order);

    // 按 ID 查询
    Order findById(Integer id);

    // 全部订单
    List<Order> findAll();

    // 按用户名/状态筛选
    List<Order> findByParams(@Param("userName") String userName,
                             @Param("status") String status);

    // 按用户 ID 查询
    List<Order> findByUserId(@Param("userId") Integer userId);

    // 按操作员 ID 查询（关联该操作员商品的订单）
    List<Order> findByOperatorId(@Param("operatorId") Integer operatorId);

    // 更新订单状态
    void updateStatus(@Param("id") Integer id, @Param("status") String status);
}
