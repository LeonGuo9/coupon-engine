package com.example.coupon.model;

/**
 * 优惠券来源枚举
 * 优先级：MEMBER > CAMPAIGN > GENERAL
 */
public enum CouponSource {

    /** 通用券（最低优先级） */
    GENERAL(1),

    /** 活动券 */
    CAMPAIGN(2),

    /** 会员专属券（最高优先级） */
    MEMBER(3);

    private final int priority;

    CouponSource(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
