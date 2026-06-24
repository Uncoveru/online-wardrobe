package com.wardrobe.backend.entity;

import java.math.BigDecimal;

public class Clothes {
    private Integer id;
    private String clothName;
    private String image;
    private Integer typeId;
    private String style;
    private BigDecimal price;

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
}
