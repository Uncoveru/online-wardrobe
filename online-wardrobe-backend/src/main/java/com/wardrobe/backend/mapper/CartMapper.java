package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    void insert(Cart cart);
    void updateAmount(@Param("id") Integer id, @Param("amount") Integer amount);
    void deleteById(Integer id);
    void deleteByIds(@Param("ids") List<Integer> ids);
    Cart findById(Integer id);
    List<Cart> findByUserId(Integer userId);
}
