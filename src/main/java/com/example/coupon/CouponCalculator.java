package com.example.coupon;

import com.example.coupon.model.CalculationResult;
import com.example.coupon.model.Coupon;
import com.example.coupon.model.Order;

import java.util.List;

/**
 * 优惠券计算器接口
 *
 * 注意：接口签名不可修改，重构时需保持兼容。
 */
public interface CouponCalculator {

    /**
     * 计算订单在使用优惠券后的最终应付金额
     *
     * @param order   订单信息
     * @param coupons 用户选择的优惠券列表
     * @return 计算结果
     */
    CalculationResult calculate(Order order, List<Coupon> coupons);
}
