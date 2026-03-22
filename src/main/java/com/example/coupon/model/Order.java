package com.example.coupon.model;

import java.util.List;

/**
 * 订单模型
 */
public class Order {

    /** 订单ID */
    private String orderId;

    /** 订单项列表 */
    private List<OrderItem> items;

    public Order() {
    }

    public Order(String orderId, List<OrderItem> items) {
        this.orderId = orderId;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", items=" + items +
                '}';
    }
}
