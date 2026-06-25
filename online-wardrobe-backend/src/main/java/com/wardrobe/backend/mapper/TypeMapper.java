package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Type;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品类型 Mapper
 */
@Mapper
public interface TypeMapper {

    // 全部类型列表
    List<Type> findAll();
}
