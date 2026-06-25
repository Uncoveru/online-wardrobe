package com.wardrobe.backend.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单实体（一对多关联 OrderItem）
 */
public class Order {

    private Integer id;               // 主键
    private String clothesDetails;    // 商品详情文本（冗余字段，方便前端展示）
    private BigDecimal price;         // 订单总价
    private String status;            // 状态码（0=未支付, 1=未发货, 2=已发货, 3=已收货）
    private Integer userId;           // 下单用户 ID
    private String address;           // 收货地址
    private String time;              // 下单时间
    private List<OrderItem> orderItems; // 订单明细

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
