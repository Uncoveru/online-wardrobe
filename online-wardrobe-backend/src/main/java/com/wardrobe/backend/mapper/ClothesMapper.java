package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Clothes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品 Mapper
 */
@Mapper
public interface ClothesMapper {

    // 新增
    void insert(Clothes clothes);

    // 更新
    void update(Clothes clothes);

    // 删除
    void deleteById(Integer id);

    // 按 ID 查询
    Clothes findById(Integer id);

    // 列表（operatorId 不为空时仅查该操作员的商品）
    List<Clothes> findAll(@Param("operatorId") Integer operatorId);

    // 多条件搜索（名称/风格/类型 + 操作员过滤）
    List<Clothes> findByParams(@Param("clothName") String clothName,
                               @Param("style") String style,
                               @Param("typeId") Integer typeId,
                               @Param("operatorId") Integer operatorId);
}
