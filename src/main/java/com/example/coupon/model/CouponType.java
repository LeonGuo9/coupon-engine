package com.example.coupon.model;

/**
 * 优惠券类型枚举
 */
public enum CouponType {

    /** 满减券：满X减Y */
    THRESHOLD_REDUCTION,

    /** 折扣券：打N折 */
    DISCOUNT,

    /** 立减券：直接减固定金额 */
    FIXED_REDUCTION,

    /** 满赠券：满X赠礼品（不影响金额，但需记录） */
    THRESHOLD_GIFT
}
