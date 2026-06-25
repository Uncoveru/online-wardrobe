package com.wardrobe.backend.entity;

/**
 * 尺码实体（每个类型对应多个尺码）
 */
public class Size {

    private Integer id;        // 主键
    private String sizeName;   // 尺码名称（S/M/L/XL/均码等）
    private Integer typeId;    // 所属类型 ID

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getSizeName() { return sizeName; }
    public void setSizeName(String sizeName) { this.sizeName = sizeName; }
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
}
