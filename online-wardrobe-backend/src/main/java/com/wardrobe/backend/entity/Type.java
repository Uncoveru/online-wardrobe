package com.wardrobe.backend.entity;

/**
 * 商品类型（上衣/裤子/裙子/鞋子等）
 */
public class Type {

    private Integer id;        // 主键
    private String typeName;   // 类型名称

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}
