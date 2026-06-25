package com.wardrobe.backend.entity;

import java.math.BigDecimal;

/**
 * 订单明细（一个订单可包含多件商品）
 */
public class OrderItem {

    private Integer id;          // 主键
    private Integer orderId;     // 所属订单 ID
    private Integer clothId;     // 商品 ID
    private String clothName;    // 商品名称（冗余，记录下单时的名称）
    private String clothSize;    // 所选尺码
    private Integer amount;      // 数量
    private BigDecimal price;    // 下单时单价
    private Integer operatorId;  // 该商品所属操作员 ID

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Integer getClothId() { return clothId; }
    public void setClothId(Integer clothId) { this.clothId = clothId; }
    public String getClothName() { return clothName; }
    public void setClothName(String clothName) { this.clothName = clothName; }
    public String getClothSize() { return clothSize; }
    public void setClothSize(String clothSize) { this.clothSize = clothSize; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }
}
