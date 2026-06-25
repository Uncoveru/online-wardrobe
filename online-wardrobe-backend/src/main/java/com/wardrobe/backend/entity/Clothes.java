package com.wardrobe.backend.entity;

import java.math.BigDecimal;

/**
 * 商品实体
 */
public class Clothes {

    private Integer id;           // 主键
    private String clothName;     // 商品名称
    private String image;         // 图片文件名
    private Integer typeId;       // 类型 ID
    private String style;         // 风格（休闲/商务/时尚等）
    private BigDecimal price;     // 价格
    private Integer operatorId;   // 添加该商品的操作员 ID

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getClothName() { return clothName; }
    public void setClothName(String clothName) { this.clothName = clothName; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }
}
