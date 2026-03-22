package com.example.coupon;

import com.example.coupon.model.*;

import java.util.*;

/**
 * 优惠券计算器实现
 *
 * 实现了优惠券叠加计算的核心逻辑，包括：
 * - 不同类型优惠券的计算
 * - 同类型优惠券的互斥选择
 * - 冲突组跨类型互斥
 * - 品类券的范围限定
 * - 叠加顺序控制
 * - 最低金额保护
 */
public class CouponCalculatorImpl implements CouponCalculator {

    @Override
    public CalculationResult calculate(Order order, List<Coupon> coupons) {
        CalculationResult result = new CalculationResult();

        // ========== 第一步：计算订单原始总价 ==========
        double totalPrice = 0;
        for (OrderItem item : order.getItems()) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        result.setOriginalPrice(totalPrice);

        double currentPrice = totalPrice;
        List<String> appliedCouponIds = new ArrayList<>();

        // ========== 第二步：过滤无效优惠券 ==========
        // 过滤过期券
        Date now = new Date();
        List<Coupon> validCoupons = new ArrayList<>();
        for (Coupon coupon : coupons) {
            if (coupon.getExpireTime().after(now)) {
                validCoupons.add(coupon);
            }
        }
        coupons = validCoupons;

        // ========== 第三步：冲突组处理 — 同一冲突组只保留一张 ==========
        Map<String, Coupon> conflictGroupSelected = new HashMap<>();
        List<Coupon> filteredCoupons = new ArrayList<>();

        for (Coupon coupon : coupons) {
            String group = coupon.getConflictGroup();
            if (group != null && !group.isEmpty()) {
                // 同一冲突组取第一张遇到的
                if (!conflictGroupSelected.containsKey(group)) {
                    conflictGroupSelected.put(group, coupon);
                    filteredCoupons.add(coupon);
                }
            } else {
                filteredCoupons.add(coupon);
            }
        }
        coupons = filteredCoupons;

        // ========== 第四步：按类型分组优惠券 ==========
        List<Coupon> thresholdReductionCoupons = new ArrayList<>();
        List<Coupon> discountCoupons = new ArrayList<>();
        List<Coupon> fixedReductionCoupons = new ArrayList<>();
        List<Coupon> giftCoupons = new ArrayList<>();

        for (Coupon coupon : coupons) {
            if (coupon.getType() == CouponType.THRESHOLD_REDUCTION) {
                thresholdReductionCoupons.add(coupon);
            } else if (coupon.getType() == CouponType.DISCOUNT) {
                discountCoupons.add(coupon);
            } else if (coupon.getType() == CouponType.FIXED_REDUCTION) {
                fixedReductionCoupons.add(coupon);
            } else if (coupon.getType() == CouponType.THRESHOLD_GIFT) {
                giftCoupons.add(coupon);
            }
        }

        // ========== 第五步：同类型互斥 — 每种类型只保留一张 ==========

        // 满减券互斥：同类型只保留一张
        Coupon selectedThresholdReduction = null;
        if (!thresholdReductionCoupons.isEmpty()) {
            // 按来源优先级降序，同来源按优惠值排序
            thresholdReductionCoupons.sort(new Comparator<Coupon>() {
                @Override
                public int compare(Coupon c1, Coupon c2) {
                    int sourceDiff = c2.getSource().getPriority() - c1.getSource().getPriority();
                    if (sourceDiff != 0) return sourceDiff;
                    return Double.compare(c1.getValue(), c2.getValue());
                }
            });
            selectedThresholdReduction = thresholdReductionCoupons.get(0);
        }

        // 折扣券互斥：同类型只保留一张
        Coupon selectedDiscount = null;
        if (!discountCoupons.isEmpty()) {
            // 按来源优先级降序，同来源按折扣率排序
            discountCoupons.sort(new Comparator<Coupon>() {
                @Override
                public int compare(Coupon c1, Coupon c2) {
                    int sourceDiff = c2.getSource().getPriority() - c1.getSource().getPriority();
                    if (sourceDiff != 0) return sourceDiff;
                    return Double.compare(c1.getValue(), c2.getValue());
                }
            });
            selectedDiscount = discountCoupons.get(0);
        }

        // 立减券互斥：同类型只保留一张
        Coupon selectedFixedReduction = null;
        if (!fixedReductionCoupons.isEmpty()) {
            // 按来源优先级降序，同来源按优惠值排序
            fixedReductionCoupons.sort(new Comparator<Coupon>() {
                @Override
                public int compare(Coupon c1, Coupon c2) {
                    int sourceDiff = c2.getSource().getPriority() - c1.getSource().getPriority();
                    if (sourceDiff != 0) return sourceDiff;
                    return Double.compare(c1.getValue(), c2.getValue());
                }
            });
            selectedFixedReduction = fixedReductionCoupons.get(0);
        }

        // 满赠券互斥：同类型只保留一张
        Coupon selectedGift = null;
        if (!giftCoupons.isEmpty()) {
            giftCoupons.sort(new Comparator<Coupon>() {
                @Override
                public int compare(Coupon c1, Coupon c2) {
                    return c2.getSource().getPriority() - c1.getSource().getPriority();
                }
            });
            selectedGift = giftCoupons.get(0);
        }

        // ========== 第六步：按业务优先级顺序计算各类型优惠 ==========

        // --- 6.1 满减券计算 ---
        if (selectedThresholdReduction != null) {
            double threshold = selectedThresholdReduction.getThreshold();
            double reduction = selectedThresholdReduction.getValue();

            // 品类券：计算适用品类的商品金额
            double applicableAmount = calculateApplicableAmount(order, selectedThresholdReduction, totalPrice);

            if (applicableAmount >= threshold) {
                currentPrice = currentPrice - reduction;
                appliedCouponIds.add(selectedThresholdReduction.getCouponId());
            }
        }

        // --- 6.2 折扣券计算 ---
        if (selectedDiscount != null) {
            double discountRate = selectedDiscount.getValue();

            // 品类券：计算适用品类的商品金额
            double applicableAmount = calculateApplicableAmount(order, selectedDiscount, totalPrice);

            // 折扣门槛检查
            double threshold = selectedDiscount.getThreshold();
            if (threshold <= 0 || applicableAmount >= threshold) {
                currentPrice = currentPrice * discountRate;
                appliedCouponIds.add(selectedDiscount.getCouponId());
            }
        }

        // --- 6.3 立减券计算 ---
        if (selectedFixedReduction != null) {
            double fixedAmount = selectedFixedReduction.getValue();

            // 品类券：计算适用品类的商品金额
            double applicableAmount = calculateApplicableAmount(order, selectedFixedReduction, totalPrice);

            // 立减门槛检查
            double threshold = selectedFixedReduction.getThreshold();
            if (threshold <= 0 || applicableAmount >= threshold) {
                currentPrice = currentPrice - fixedAmount;
                appliedCouponIds.add(selectedFixedReduction.getCouponId());
            }
        }

        // --- 6.4 满赠券处理（不影响金额） ---
        if (selectedGift != null) {
            double threshold = selectedGift.getThreshold();
            double applicableAmount = calculateApplicableAmount(order, selectedGift, totalPrice);
            if (applicableAmount >= threshold) {
                appliedCouponIds.add(selectedGift.getCouponId());
            }
        }

        // ========== 第七步：最低金额保护 ==========
        if (currentPrice < 0.01) {
            currentPrice = 0.01;
        }

        // ========== 第八步：设置结果 ==========
        result.setFinalPrice(currentPrice);
        result.setTotalDiscount(totalPrice - currentPrice);
        result.setAppliedCouponIds(appliedCouponIds);

        return result;
    }

    /**
     * 计算优惠券适用的商品金额
     * 如果优惠券指定了品类，只计算对应品类商品的金额；否则返回全品类总价
     */
    private double calculateApplicableAmount(Order order, Coupon coupon, double totalPrice) {
        List<String> categories = coupon.getApplicableCategories();
        if (categories == null || categories.isEmpty()) {
            return totalPrice;
        }

        double amount = 0;
        for (OrderItem item : order.getItems()) {
            if (item.getCategory() != null && categories.contains(item.getCategory())) {
                amount += item.getPrice() * item.getQuantity();
            }
        }
        return amount;
    }
}
