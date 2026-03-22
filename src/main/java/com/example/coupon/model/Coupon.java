package com.example.coupon.model;

import java.util.Date;
import java.util.List;

/**
 * 优惠券模型
 */
public class Coupon {

    /** 优惠券ID */
    private String couponId;

    /** 优惠券名称 */
    private String name;

    /** 优惠券类型 */
    private CouponType type;

    /** 优惠券来源 */
    private CouponSource source;

    /** 使用门槛金额（满X元可用），0表示无门槛 */
    private double threshold;

    /**
     * 优惠值，含义随类型变化：
     * - THRESHOLD_REDUCTION: 减免金额（如30表示减30元）
     * - DISCOUNT: 折扣率（如0.8表示8折）
     * - FIXED_REDUCTION: 固定减免金额（如15表示减15元）
     * - THRESHOLD_GIFT: 无金额意义，固定为0
     */
    private double value;

    /** 赠品描述（仅 THRESHOLD_GIFT 类型使用） */
    private String giftDescription;

    /** 适用品类列表（null或空表示全品类通用） */
    private List<String> applicableCategories;

    /** 互斥组标识（同一互斥组的券即使类型不同也只能用一张，null表示无互斥组） */
    private String conflictGroup;

    /** 过期时间 */
    private Date expireTime;

    public Coupon() {
    }

    public Coupon(String couponId, String name, CouponType type, CouponSource source,
                  double threshold, double value) {
        this.couponId = couponId;
        this.name = name;
        this.type = type;
        this.source = source;
        this.threshold = threshold;
        this.value = value;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public CouponSource getSource() {
        return source;
    }

    public void setSource(CouponSource source) {
        this.source = source;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getGiftDescription() {
        return giftDescription;
    }

    public void setGiftDescription(String giftDescription) {
        this.giftDescription = giftDescription;
    }

    public List<String> getApplicableCategories() {
        return applicableCategories;
    }

    public void setApplicableCategories(List<String> applicableCategories) {
        this.applicableCategories = applicableCategories;
    }

    public String getConflictGroup() {
        return conflictGroup;
    }

    public void setConflictGroup(String conflictGroup) {
        this.conflictGroup = conflictGroup;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "couponId='" + couponId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", source=" + source +
                ", threshold=" + threshold +
                ", value=" + value +
                '}';
    }
}
