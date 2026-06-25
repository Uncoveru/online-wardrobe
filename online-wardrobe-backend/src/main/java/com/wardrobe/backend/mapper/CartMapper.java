package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车 Mapper
 */
@Mapper
public interface CartMapper {

    // 加入购物车
    void insert(Cart cart);

    // 修改数量
    void updateAmount(@Param("id") Integer id, @Param("amount") Integer amount);

    // 删除单条
    void deleteById(Integer id);

    // 批量删除（结算后清空）
    void deleteByIds(@Param("ids") List<Integer> ids);

    // 按 ID 查询
    Cart findById(Integer id);

    // 按用户查询购物车列表
    List<Cart> findByUserId(Integer userId);
}
