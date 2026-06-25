package com.wardrobe.backend.entity;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private Integer id;
    private String clothesDetails;
    private BigDecimal price;
    private String status;
    private Integer userId;
    private String address;
    private String time;
    private List<OrderItem> orderItems;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getClothesDetails() { return clothesDetails; }
    public void setClothesDetails(String clothesDetails) { this.clothesDetails = clothesDetails; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
