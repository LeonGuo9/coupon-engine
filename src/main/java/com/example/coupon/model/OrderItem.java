package com.example.coupon.model;

/**
 * 订单项模型
 */
public class OrderItem {

    /** 商品ID */
    private String skuId;

    /** 商品名称 */
    private String skuName;

    /** 单价（元） */
    private double price;

    /** 数量 */
    private int quantity;

    /** 商品品类（如 "electronics"、"accessories"） */
    private String category;

    public OrderItem() {
    }

    public OrderItem(String skuId, String skuName, double price, int quantity) {
        this.skuId = skuId;
        this.skuName = skuName;
        this.price = price;
        this.quantity = quantity;
    }

    public OrderItem(String skuId, String skuName, double price, int quantity, String category) {
        this.skuId = skuId;
        this.skuName = skuName;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "skuId='" + skuId + '\'' +
                ", skuName='" + skuName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", category='" + category + '\'' +
                '}';
    }
}
