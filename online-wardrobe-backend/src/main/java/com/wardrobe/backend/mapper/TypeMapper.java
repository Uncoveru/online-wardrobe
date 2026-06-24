package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Type;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TypeMapper {
    List<Type> findAll();
}
