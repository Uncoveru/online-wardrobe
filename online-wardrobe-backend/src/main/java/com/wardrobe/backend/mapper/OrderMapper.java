package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Order order);
    Order findById(Integer id);
    List<Order> findAll();
    List<Order> findByParams(@Param("userName") String userName,
                             @Param("status") String status);
    List<Order> findByUserId(@Param("userId") Integer userId);
    void updateStatus(@Param("id") Integer id, @Param("status") String status);
}
