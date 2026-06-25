package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.Size;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 尺码 Mapper
 */
@Mapper
public interface SizeMapper {

    // 按商品类型查尺码列表
    List<Size> findByTypeId(Integer typeId);
}
