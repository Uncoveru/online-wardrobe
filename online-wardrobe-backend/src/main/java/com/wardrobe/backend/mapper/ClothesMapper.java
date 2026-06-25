package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Clothes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClothesMapper {
    void insert(Clothes clothes);
    void update(Clothes clothes);
    void deleteById(Integer id);
    Clothes findById(Integer id);
    List<Clothes> findAll(@Param("operatorId") Integer operatorId);
    List<Clothes> findByParams(@Param("clothName") String clothName,
                               @Param("style") String style,
                               @Param("typeId") Integer typeId,
                               @Param("operatorId") Integer operatorId);
}
