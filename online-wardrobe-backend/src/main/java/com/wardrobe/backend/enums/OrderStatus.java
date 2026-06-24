package com.wardrobe.backend.enums;

public enum OrderStatus {
    UNPAID("0", "未支付"),
    PAID("1", "未发货"),
    SHIPPED("2", "已发货"),
    RECEIVED("3", "已收货");

    private final String code;
    private final String label;

    OrderStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
