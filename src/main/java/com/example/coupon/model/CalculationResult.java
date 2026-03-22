package com.example.coupon.model;

import java.util.List;

/**
 * 计算结果模型
 *
 * 注意：当前只包含基础字段，缺少计算明细信息。
 * 候选人需要扩展此类，添加每张优惠券的优惠明细和未使用原因等信息。
 */
public class CalculationResult {

    /** 原始总价 */
    private double originalPrice;

    /** 最终应付金额 */
    private double finalPrice;

    /** 总优惠金额 */
    private double totalDiscount;

    /** 使用的优惠券ID列表 */
    private List<String> appliedCouponIds;

    public CalculationResult() {
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public List<String> getAppliedCouponIds() {
        return appliedCouponIds;
    }

    public void setAppliedCouponIds(List<String> appliedCouponIds) {
        this.appliedCouponIds = appliedCouponIds;
    }

    @Override
    public String toString() {
        return "CalculationResult{" +
                "originalPrice=" + originalPrice +
                ", finalPrice=" + finalPrice +
                ", totalDiscount=" + totalDiscount +
                ", appliedCouponIds=" + appliedCouponIds +
                '}';
    }
}
