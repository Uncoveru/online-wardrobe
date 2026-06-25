package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    void insert(OrderItem item);
    void insertBatch(@Param("items") List<OrderItem> items);
    List<OrderItem> findByOrderId(@Param("orderId") Integer orderId);
    List<OrderItem> findByOrderIds(@Param("orderIds") List<Integer> orderIds);
}
