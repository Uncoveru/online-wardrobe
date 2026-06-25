package com.wardrobe.backend.entity;

import java.math.BigDecimal;

/**
 * 购物车条目
 */
public class Cart {

    private Integer id;          // 主键
    private Integer clothId;     // 商品 ID
    private String clothSize;    // 所选尺码
    private Integer amount;      // 数量
    private Integer userId;      // 用户 ID
    private String date;         // 加入时间

    // 以下为关联查询字段，非表中字段
    private String clothName;    // 商品名称（JOIN）
    private String image;        // 商品图片（JOIN）
    private BigDecimal price;    // 商品单价（JOIN）

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getClothId() { return clothId; }
    public void setClothId(Integer clothId) { this.clothId = clothId; }
    public String getClothSize() { return clothSize; }
    public void setClothSize(String clothSize) { this.clothSize = clothSize; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getClothName() { return clothName; }
    public void setClothName(String clothName) { this.clothName = clothName; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
