package com.wardrobe.backend.entity;

import java.math.BigDecimal;

public class Cart {
    private Integer id;
    private Integer clothId;
    private String clothSize;
    private Integer amount;
    private Integer userId;
    private String date;

    private String clothName;
    private String image;
    private BigDecimal price;

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
