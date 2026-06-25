package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单明细 Mapper
 */
@Mapper
public interface OrderItemMapper {

    // 插入单条
    void insert(OrderItem item);

    // 批量插入
    void insertBatch(@Param("items") List<OrderItem> items);

    // 按订单 ID 查询明细
    List<OrderItem> findByOrderId(@Param("orderId") Integer orderId);

    // 按订单 ID 列表批量查询明细
    List<OrderItem> findByOrderIds(@Param("orderIds") List<Integer> orderIds);
}
