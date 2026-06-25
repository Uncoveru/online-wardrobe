package com.wardrobe.backend.enums;

/**
 * 订单状态枚举：未支付 → 未发货 → 已发货 → 已收货
 */
public enum OrderStatus {
    UNPAID("0", "未支付"),
    PAID("1", "未发货"),
    SHIPPED("2", "已发货"),
    RECEIVED("3", "已收货");

    // 状态码（存入数据库）
    private final String code;
    // 中文显示名
    private final String label;

    OrderStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
