package com.mall.portal.service;

import com.mall.mbg.mapper.SmsCouponMapper;
import com.mall.mbg.mapper.UmsMemberCouponMapper;
import com.mall.mbg.model.SmsCoupon;
import com.mall.mbg.model.UmsMemberCoupon;
import com.mall.portal.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 优惠券服务测试类 - TDD模式
 *
 * 测试场景：
 * 1. 正常领取优惠券
 * 2. 超出每人限领数量
 * 3. 优惠券已领完
 * 4. 优惠券已过期
 * 5. 订单满足门槛使用优惠券
 * 6. 订单不满足门槛
 * 7. 计算最优优惠券
 * 8. 订单取消返还优惠券
 */
class CouponServiceTest {

    private SmsCouponMapper couponMapper;
    private UmsMemberCouponMapper memberCouponMapper;
    private CouponServiceImpl couponService;

    private static final Long COUPON_ID = 1L;
    private static final Long MEMBER_ID = 10001L;

    @BeforeEach
    void setUp() {
        couponMapper = mock(SmsCouponMapper.class);
        memberCouponMapper = mock(UmsMemberCouponMapper.class);
        couponService = new CouponServiceImpl(couponMapper, memberCouponMapper);
    }

    @Test
    @DisplayName("TC001: 正常领取满减券 - 应成功")
    void receiveCoupon_WhenValid_ShouldSuccess() {
        SmsCoupon coupon = createFullReductionCoupon();
        when(couponMapper.selectById(COUPON_ID)).thenReturn(coupon);
        when(memberCouponMapper.selectCount(any())).thenReturn(0L);
        when(memberCouponMapper.insert(any())).thenReturn(1);

        boolean result = couponService.receiveCoupon(COUPON_ID, MEMBER_ID);

        assertTrue(result, "正常领取应成功");
        verify(couponMapper).updateById(any());
    }

    @Test
    @DisplayName("TC002: 超出每人限领 - 应失败")
    void receiveCoupon_WhenExceedLimit_ShouldFail() {
        SmsCoupon coupon = createFullReductionCoupon();
        coupon.setPerLimit(1);
        when(couponMapper.selectById(COUPON_ID)).thenReturn(coupon);
        when(memberCouponMapper.selectCount(any())).thenReturn(1L);

        boolean result = couponService.receiveCoupon(COUPON_ID, MEMBER_ID);

        assertFalse(result, "超出限领应失败");
    }

    @Test
    @DisplayName("TC003: 优惠券已领完 - 应失败")
    void receiveCoupon_WhenNoRemain_ShouldFail() {
        SmsCoupon coupon = createFullReductionCoupon();
        coupon.setRemainCount(0);
        when(couponMapper.selectById(COUPON_ID)).thenReturn(coupon);

        boolean result = couponService.receiveCoupon(COUPON_ID, MEMBER_ID);

        assertFalse(result, "已领完应失败");
    }

    @Test
    @DisplayName("TC004: 优惠券已过期 - 应失败")
    void receiveCoupon_WhenExpired_ShouldFail() {
        SmsCoupon coupon = createFullReductionCoupon();
        coupon.setEndTime(LocalDateTime.now().minusDays(1));
        when(couponMapper.selectById(COUPON_ID)).thenReturn(coupon);

        boolean result = couponService.receiveCoupon(COUPON_ID, MEMBER_ID);

        assertFalse(result, "已过期应失败");
    }

    @Test
    @DisplayName("TC005: 订单满足门槛使用满减券 - 应成功")
    void useCoupon_WhenOrderMeetMinPoint_ShouldSuccess() {
        SmsCoupon coupon = createFullReductionCoupon();
        UmsMemberCoupon memberCoupon = createMemberCoupon();
        when(memberCouponMapper.selectById(1L)).thenReturn(memberCoupon);
        when(couponMapper.selectById(COUPON_ID)).thenReturn(coupon);

        BigDecimal orderAmount = new BigDecimal("200");
        BigDecimal discount = couponService.calculateDiscount(COUPON_ID, orderAmount);

        assertEquals(new BigDecimal("20"), discount, "应减20元");
    }

    @Test
    @DisplayName("TC006: 订单不满足门槛 - 应返回0")
    void useCoupon_WhenOrderNotMeetMinPoint_ShouldReturnZero() {
        SmsCoupon coupon = createFullReductionCoupon();
        when(couponMapper.selectById(COUPON_ID)).thenReturn(coupon);

        BigDecimal orderAmount = new BigDecimal("50");
        BigDecimal discount = couponService.calculateDiscount(COUPON_ID, orderAmount);

        assertEquals(BigDecimal.ZERO, discount, "不满足门槛应返回0");
    }

    @Test
    @DisplayName("TC007: 使用折扣券 - 应正确计算折扣")
    void useDiscountCoupon_ShouldCalculateCorrectly() {
        SmsCoupon coupon = SmsCoupon.builder()
                .id(COUPON_ID)
                .type(2)
                .amount(new BigDecimal("0.8"))
                .minPoint(new BigDecimal("100"))
                .maxDiscount(new BigDecimal("50"))
                .status(1)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        when(couponMapper.selectById(COUPON_ID)).thenReturn(coupon);

        BigDecimal orderAmount = new BigDecimal("300");
        BigDecimal discount = couponService.calculateDiscount(COUPON_ID, orderAmount);

        // 300 * 0.2 = 60，但最大优惠50，所以返回50
        assertEquals(new BigDecimal("50"), discount, "折扣券应减50元");
    }

    @Test
    @DisplayName("TC008: 使用无门槛券 - 应直接减免")
    void useNoThresholdCoupon_ShouldDirectDiscount() {
        SmsCoupon coupon = SmsCoupon.builder()
                .id(COUPON_ID)
                .type(3)
                .amount(new BigDecimal("10"))
                .minPoint(BigDecimal.ZERO)
                .status(1)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        when(couponMapper.selectById(COUPON_ID)).thenReturn(coupon);

        BigDecimal orderAmount = new BigDecimal("50");
        BigDecimal discount = couponService.calculateDiscount(COUPON_ID, orderAmount);

        assertEquals(new BigDecimal("10"), discount, "无门槛券应减10元");
    }

    @Test
    @DisplayName("TC009: 计算最优优惠券 - 应选择优惠最大的")
    void selectBestCoupon_ShouldSelectMaxDiscount() {
        SmsCoupon coupon1 = createFullReductionCoupon(); // 满100减20
        SmsCoupon coupon2 = SmsCoupon.builder()
                .id(2L)
                .type(3)
                .amount(new BigDecimal("30"))
                .minPoint(BigDecimal.ZERO)
                .status(1)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();

        when(couponMapper.selectBatchIds(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(coupon1, coupon2));

        BigDecimal orderAmount = new BigDecimal("200");
        Long bestCouponId = couponService.selectBestCoupon(Arrays.asList(1L, 2L), orderAmount);

        assertEquals(2L, bestCouponId, "应选择优惠更大的无门槛30元券");
    }

    @Test
    @DisplayName("TC010: 订单取消返还优惠券 - 应成功")
    void returnCoupon_WhenOrderCancelled_ShouldSuccess() {
        UmsMemberCoupon memberCoupon = createMemberCoupon();
        memberCoupon.setStatus(1);
        memberCoupon.setOrderId(100L);
        when(memberCouponMapper.selectById(1L)).thenReturn(memberCoupon);
        when(memberCouponMapper.updateById(any())).thenReturn(1);

        boolean result = couponService.returnCoupon(1L);

        assertTrue(result, "返还优惠券应成功");
    }

    private SmsCoupon createFullReductionCoupon() {
        return SmsCoupon.builder()
                .id(COUPON_ID)
                .name("满100减20")
                .type(1)
                .amount(new BigDecimal("20"))
                .minPoint(new BigDecimal("100"))
                .totalCount(100)
                .remainCount(50)
                .perLimit(3)
                .status(1)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
    }

    private UmsMemberCoupon createMemberCoupon() {
        return UmsMemberCoupon.builder()
                .id(1L)
                .memberId(MEMBER_ID)
                .couponId(COUPON_ID)
                .status(0)
                .getTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusDays(7))
                .build();
    }
}
