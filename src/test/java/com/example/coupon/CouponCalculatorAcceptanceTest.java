package com.example.coupon;

import com.example.coupon.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * 优惠券计算器验收测试
 *
 * 共 12 个用例，候选人需要修复代码使所有测试通过。
 *
 * 注意：本测试仅通过公开接口 CouponCalculator.calculate() 进行验证，
 * 不依赖任何内部实现细节。重构时保证接口不变即可。
 */
public class CouponCalculatorAcceptanceTest {

    private CouponCalculator calculator;

    @Before
    public void setUp() {
        calculator = new CouponCalculatorImpl();
    }

    // ==================== 基础功能测试 ====================

    @Test
    public void testSingleDiscount_shouldApplyCorrectly() {
        // 订单总价500元，使用8折券 → 应付400元
        Order order = createSimpleOrder(500.0);
        List<Coupon> coupons = Arrays.asList(
                createCoupon("C001", "8折优惠券", CouponType.DISCOUNT, CouponSource.GENERAL, 0, 0.8)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(500.0, result.getOriginalPrice(), 0.001);
        assertEquals(400.0, result.getFinalPrice(), 0.001);
        assertEquals(100.0, result.getTotalDiscount(), 0.001);
        assertTrue(result.getAppliedCouponIds().contains("C001"));
    }

    @Test
    public void testSingleThresholdReduction_shouldApplyCorrectly() {
        // 订单总价500元，使用满200减30券 → 应付470元
        Order order = createSimpleOrder(500.0);
        List<Coupon> coupons = Arrays.asList(
                createCoupon("C002", "满200减30", CouponType.THRESHOLD_REDUCTION, CouponSource.GENERAL, 200, 30)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(500.0, result.getOriginalPrice(), 0.001);
        assertEquals(470.0, result.getFinalPrice(), 0.001);
        assertEquals(30.0, result.getTotalDiscount(), 0.001);
        assertTrue(result.getAppliedCouponIds().contains("C002"));
    }

    @Test
    public void testFixedReduction_shouldApplyCorrectly() {
        // 订单总价500元，使用立减15券 → 应付485元
        Order order = createSimpleOrder(500.0);
        List<Coupon> coupons = Arrays.asList(
                createCoupon("C003", "立减15元", CouponType.FIXED_REDUCTION, CouponSource.GENERAL, 0, 15)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(500.0, result.getOriginalPrice(), 0.001);
        assertEquals(485.0, result.getFinalPrice(), 0.001);
        assertEquals(15.0, result.getTotalDiscount(), 0.001);
        assertTrue(result.getAppliedCouponIds().contains("C003"));
    }

    // ==================== 叠加与互斥测试 ====================

    @Test
    public void testStackingOrder_discountThenReduction() {
        // 订单总价500元，同时使用8折券 + 满200减30 + 立减10
        // 正确顺序：先折后减
        // 折扣：500 * 0.8 = 400
        // 满减：400 >= 200，400 - 30 = 370
        // 立减：370 - 10 = 360
        // 最终应付：360元
        Order order = createSimpleOrder(500.0);
        List<Coupon> coupons = Arrays.asList(
                createCoupon("C001", "8折优惠券", CouponType.DISCOUNT, CouponSource.GENERAL, 0, 0.8),
                createCoupon("C002", "满200减30", CouponType.THRESHOLD_REDUCTION, CouponSource.GENERAL, 200, 30),
                createCoupon("C003", "立减10元", CouponType.FIXED_REDUCTION, CouponSource.GENERAL, 0, 10)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(500.0, result.getOriginalPrice(), 0.001);
        assertEquals(360.0, result.getFinalPrice(), 0.001);
        assertEquals(140.0, result.getTotalDiscount(), 0.001);
        assertTrue(result.getAppliedCouponIds().contains("C001"));
        assertTrue(result.getAppliedCouponIds().contains("C002"));
        assertTrue(result.getAppliedCouponIds().contains("C003"));
    }

    @Test
    public void testMutualExclusion_shouldPickBestReduction() {
        // 订单总价500元，两张满减券：满200减30 和 满200减50
        // 两张来源相同（GENERAL），应选优惠力度最大的 满200减50
        // 最终应付：500 - 50 = 450元
        Order order = createSimpleOrder(500.0);
        List<Coupon> coupons = Arrays.asList(
                createCoupon("C010", "满200减30", CouponType.THRESHOLD_REDUCTION, CouponSource.GENERAL, 200, 30),
                createCoupon("C011", "满200减50", CouponType.THRESHOLD_REDUCTION, CouponSource.GENERAL, 200, 50)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(500.0, result.getOriginalPrice(), 0.001);
        assertEquals(450.0, result.getFinalPrice(), 0.001);
        assertEquals(50.0, result.getTotalDiscount(), 0.001);
        assertTrue("应选择优惠力度最大的券C011", result.getAppliedCouponIds().contains("C011"));
        assertFalse("不应使用券C010", result.getAppliedCouponIds().contains("C010"));
    }

    @Test
    public void testMutualExclusion_shouldPickBestDiscount() {
        // 订单总价500元，两张折扣券：8折(0.8) 和 7折(0.7)
        // 两张来源相同（GENERAL），应选优惠力度最大的 7折(0.7)
        // 最终应付：500 * 0.7 = 350元
        Order order = createSimpleOrder(500.0);
        List<Coupon> coupons = Arrays.asList(
                createCoupon("C020", "8折券", CouponType.DISCOUNT, CouponSource.GENERAL, 0, 0.8),
                createCoupon("C021", "7折券", CouponType.DISCOUNT, CouponSource.GENERAL, 0, 0.7)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(500.0, result.getOriginalPrice(), 0.001);
        assertEquals(350.0, result.getFinalPrice(), 0.001);
        assertEquals(150.0, result.getTotalDiscount(), 0.001);
        assertTrue("应选择优惠力度最大的券C021(7折)", result.getAppliedCouponIds().contains("C021"));
        assertFalse("不应使用券C020(8折)", result.getAppliedCouponIds().contains("C020"));
    }

    @Test
    public void testMutualExclusion_sourcePriorityOverridesValue() {
        // 订单总价500元，两张满减券：MEMBER满200减30 和 GENERAL满200减50
        // MEMBER 优先级高于 GENERAL，所以即使 GENERAL 的优惠更大，也应选 MEMBER
        // 最终应付：500 - 30 = 470元
        Order order = createSimpleOrder(500.0);
        List<Coupon> coupons = Arrays.asList(
                createCoupon("C030", "会员满减30", CouponType.THRESHOLD_REDUCTION, CouponSource.MEMBER, 200, 30),
                createCoupon("C031", "通用满减50", CouponType.THRESHOLD_REDUCTION, CouponSource.GENERAL, 200, 50)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(470.0, result.getFinalPrice(), 0.001);
        assertTrue("应选择来源优先级更高的MEMBER券", result.getAppliedCouponIds().contains("C030"));
    }

    // ==================== 边界与异常测试 ====================

    @Test
    public void testEdgeCase_nullAndEmptyCoupons() {
        // 空券列表和null均不应抛异常，返回原价
        Order order = createSimpleOrder(500.0);

        CalculationResult result1 = calculator.calculate(order, Collections.<Coupon>emptyList());
        assertEquals(500.0, result1.getFinalPrice(), 0.001);
        assertEquals(0.0, result1.getTotalDiscount(), 0.001);

        CalculationResult result2 = calculator.calculate(order, null);
        assertEquals(500.0, result2.getFinalPrice(), 0.001);
        assertEquals(0.0, result2.getTotalDiscount(), 0.001);
    }

    @Test
    public void testEdgeCase_minimumPriceProtection() {
        // 订单总价10元，使用立减9.99 + 满减5 → 确保最低金额不低于0.01
        Order order = createSimpleOrder(10.0);
        List<Coupon> coupons = Arrays.asList(
                createCoupon("C040", "立减9.99", CouponType.FIXED_REDUCTION, CouponSource.GENERAL, 0, 9.99),
                createCoupon("C041", "满5减5", CouponType.THRESHOLD_REDUCTION, CouponSource.GENERAL, 5, 5)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        assertTrue("最终金额不得低于0.01", result.getFinalPrice() >= 0.01);
    }

    // ==================== 品类券与冲突组测试 ====================

    @Test
    public void testCategoryCoupon_shouldOnlyApplyToMatchingCategory() {
        // 订单: electronics 300元 + accessories 200元 = 500元
        // 满减券限定 electronics 品类，满200减40
        // electronics部分=300 >= 200，可用，总价减40 → 500 - 40 = 460
        Order order = createCategoryOrder();
        Coupon categoryCoupon = createCoupon("C050", "电子产品满减", CouponType.THRESHOLD_REDUCTION,
                CouponSource.GENERAL, 200, 40);
        categoryCoupon.setApplicableCategories(Arrays.asList("electronics"));

        List<Coupon> coupons = Arrays.asList(categoryCoupon);
        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(500.0, result.getOriginalPrice(), 0.001);
        assertEquals(460.0, result.getFinalPrice(), 0.001);
        assertTrue(result.getAppliedCouponIds().contains("C050"));
    }

    @Test
    public void testConflictGroup_shouldOnlyUseOne() {
        // 订单总价500元
        // 满减券满200减120（冲突组spring_sale） + 折扣券8折（冲突组spring_sale）
        // 同一冲突组只能用一张，应取优惠力度最大的
        // 满减优惠：120元
        // 8折优惠：500 * 0.2 = 100元
        // 应选满减120（优惠120 > 100），最终应付380元
        Order order = createSimpleOrder(500.0);
        Coupon reductionCoupon = createCoupon("C061", "春促满减120", CouponType.THRESHOLD_REDUCTION,
                CouponSource.CAMPAIGN, 200, 120);
        reductionCoupon.setConflictGroup("spring_sale");

        Coupon discountCoupon = createCoupon("C060", "春促8折", CouponType.DISCOUNT,
                CouponSource.CAMPAIGN, 0, 0.8);
        discountCoupon.setConflictGroup("spring_sale");

        List<Coupon> coupons = Arrays.asList(discountCoupon, reductionCoupon);
        CalculationResult result = calculator.calculate(order, coupons);

        assertEquals(380.0, result.getFinalPrice(), 0.001);
        assertEquals(1, result.getAppliedCouponIds().size());
        assertTrue("应选择优惠力度更大的满减120券", result.getAppliedCouponIds().contains("C061"));
    }

    // ==================== 精度测试 ====================

    @Test
    public void testPrecision_floatingPointAccuracy() {
        // 经典浮点精度场景：19.90 * 3 = 59.70（double下可能为59.699999...）
        // 使用立减5 → 应该精确得到 54.70
        OrderItem item = new OrderItem("SKU001", "数据线", 19.90, 3);
        Order order = new Order("ORDER_P", Arrays.asList(item));

        List<Coupon> coupons = Arrays.asList(
                createCoupon("C070", "立减5元", CouponType.FIXED_REDUCTION, CouponSource.GENERAL, 0, 5)
        );

        CalculationResult result = calculator.calculate(order, coupons);

        // 金额应该精确，不能有浮点误差
        assertEquals("19.90 * 3 - 5 应精确等于 54.70", 54.70, result.getFinalPrice(), 0.001);
        assertEquals("原始价格应精确等于 59.70", 59.70, result.getOriginalPrice(), 0.001);
    }

    // ==================== 分摊测试 ====================

    // 以下测试验证"优惠分摊到商品行"功能
    // 候选人需要扩展 CalculationResult，添加分摊明细数据结构

    // 分摊测试由候选人根据 README 需求自行编写
    // 这里提供分摊功能的验收标准：
    // 1. 分摊结果中每个SKU的分摊金额之和 = 该券的优惠总额
    // 2. 品类券只分摊到对应品类的商品行
    // 3. 分摊金额精确到分（两位小数）
    // 4. 尾差归入金额最大的商品行

    // ==================== 辅助方法 ====================

    /**
     * 创建包含单个商品的简单订单
     */
    private Order createSimpleOrder(double totalPrice) {
        OrderItem item = new OrderItem("SKU001", "测试商品", totalPrice, 1);
        return new Order("ORDER001", Arrays.asList(item));
    }

    /**
     * 创建包含多品类商品的订单
     * electronics: 300元, accessories: 200元, 总价500元
     */
    private Order createCategoryOrder() {
        OrderItem electronics = new OrderItem("SKU_E1", "蓝牙耳机", 300.0, 1, "electronics");
        OrderItem accessories = new OrderItem("SKU_A1", "手机壳", 200.0, 1, "accessories");
        return new Order("ORDER_CAT", Arrays.asList(electronics, accessories));
    }

    /**
     * 创建优惠券
     */
    private Coupon createCoupon(String id, String name, CouponType type,
                                CouponSource source, double threshold, double value) {
        return new Coupon(id, name, type, source, threshold, value);
    }
}
