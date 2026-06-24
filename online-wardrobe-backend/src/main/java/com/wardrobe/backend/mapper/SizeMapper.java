package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Size;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SizeMapper {
    List<Size> findByTypeId(Integer typeId);
}
