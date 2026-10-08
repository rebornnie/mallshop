package com.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.mbg.mapper.SmsCouponMapper;
import com.mall.mbg.mapper.UmsMemberCouponMapper;
import com.mall.mbg.model.SmsCoupon;
import com.mall.mbg.model.UmsMemberCoupon;
import com.mall.portal.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 优惠券服务实现类
 *
 * 支持三种优惠券类型：
 * 1. 满减券：订单金额 >= 门槛金额，减免固定金额
 * 2. 折扣券：订单金额 * 折扣率，有最大优惠上限
 * 3. 无门槛券：直接减免固定金额
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final SmsCouponMapper couponMapper;
    private final UmsMemberCouponMapper memberCouponMapper;

    /**
     * 优惠券类型：1满减券 2折扣券 3无门槛券
     */
    private static final int COUPON_TYPE_FULL_REDUCTION = 1;
    private static final int COUPON_TYPE_DISCOUNT = 2;
    private static final int COUPON_TYPE_NO_THRESHOLD = 3;

    @Override
    @Transactional
    public boolean receiveCoupon(Long couponId, Long memberId) {
        SmsCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            log.warn("优惠券不存在, couponId={}", couponId);
            return false;
        }

        if (coupon.getStatus() != 1) {
            log.warn("优惠券已禁用, couponId={}", couponId);
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            log.warn("优惠券不在有效期内, couponId={}", couponId);
            return false;
        }

        if (coupon.getRemainCount() <= 0) {
            log.warn("优惠券已领完, couponId={}", couponId);
            return false;
        }

        LambdaQueryWrapper<UmsMemberCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberCoupon::getMemberId, memberId)
                .eq(UmsMemberCoupon::getCouponId, couponId);
        Long receivedCount = memberCouponMapper.selectCount(wrapper);
        if (receivedCount >= coupon.getPerLimit()) {
            log.warn("超出每人限领数量, memberId={}, couponId={}, limit={}", memberId, couponId, coupon.getPerLimit());
            return false;
        }

        UmsMemberCoupon memberCoupon = UmsMemberCoupon.builder()
                .memberId(memberId)
                .couponId(couponId)
                .status(0)
                .getTime(now)
                .expireTime(coupon.getEndTime())
                .build();
        memberCouponMapper.insert(memberCoupon);

        coupon.setRemainCount(coupon.getRemainCount() - 1);
        couponMapper.updateById(coupon);

        log.info("领取优惠券成功, memberId={}, couponId={}", memberId, couponId);
        return true;
    }

    @Override
    public List<SmsCoupon> getAvailableCoupons() {
        LambdaQueryWrapper<SmsCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCoupon::getStatus, 1)
                .gt(SmsCoupon::getRemainCount, 0)
                .le(SmsCoupon::getStartTime, LocalDateTime.now())
                .ge(SmsCoupon::getEndTime, LocalDateTime.now())
                .orderByDesc(SmsCoupon::getCreateTime);
        return couponMapper.selectList(wrapper);
    }

    @Override
    public List<UmsMemberCoupon> getMyCoupons(Long memberId, Integer status) {
        LambdaQueryWrapper<UmsMemberCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberCoupon::getMemberId, memberId);
        if (status != null) {
            wrapper.eq(UmsMemberCoupon::getStatus, status);
        }
        wrapper.orderByDesc(UmsMemberCoupon::getGetTime);
        return memberCouponMapper.selectList(wrapper);
    }

    @Override
    public BigDecimal calculateDiscount(Long couponId, BigDecimal orderAmount) {
        SmsCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            return BigDecimal.ZERO;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            return BigDecimal.ZERO;
        }

        if (orderAmount.compareTo(coupon.getMinPoint()) < 0) {
            return BigDecimal.ZERO;
        }

        return doCalculateDiscount(coupon, orderAmount);
    }

    /**
     * 基于优惠券对象直接计算优惠金额（避免重复查询数据库）
     */
    private BigDecimal doCalculateDiscount(SmsCoupon coupon, BigDecimal orderAmount) {
        return switch (coupon.getType()) {
            case COUPON_TYPE_FULL_REDUCTION, COUPON_TYPE_NO_THRESHOLD ->
                    coupon.getAmount().min(orderAmount);
            case COUPON_TYPE_DISCOUNT -> {
                BigDecimal discountRate = BigDecimal.ONE.subtract(coupon.getAmount());
                BigDecimal discount = orderAmount.multiply(discountRate);
                if (coupon.getMaxDiscount() != null) {
                    discount = discount.min(coupon.getMaxDiscount());
                }
                yield discount.min(orderAmount);
            }
            default -> BigDecimal.ZERO;
        };
    }

    @Override
    public Long selectBestCoupon(List<Long> couponIds, BigDecimal orderAmount) {
        if (couponIds == null || couponIds.isEmpty()) {
            return null;
        }

        List<SmsCoupon> coupons = couponMapper.selectBatchIds(couponIds);
        return coupons.stream()
                .filter(c -> c.getStatus() == 1)
                .filter(c -> {
                    LocalDateTime now = LocalDateTime.now();
                    return !now.isBefore(c.getStartTime()) && !now.isAfter(c.getEndTime());
                })
                .filter(c -> orderAmount.compareTo(c.getMinPoint()) >= 0)
                .max(Comparator.comparing(c -> doCalculateDiscount(c, orderAmount)))
                .map(SmsCoupon::getId)
                .orElse(null);
    }

    @Override
    @Transactional
    public boolean useCoupon(Long memberCouponId, Long orderId) {
        UmsMemberCoupon memberCoupon = memberCouponMapper.selectById(memberCouponId);
        if (memberCoupon == null || memberCoupon.getStatus() != 0) {
            log.warn("优惠券不可用, memberCouponId={}", memberCouponId);
            return false;
        }

        memberCoupon.setStatus(1);
        memberCoupon.setOrderId(orderId);
        memberCoupon.setUseTime(LocalDateTime.now());
        memberCouponMapper.updateById(memberCoupon);

        log.info("使用优惠券成功, memberCouponId={}, orderId={}", memberCouponId, orderId);
        return true;
    }

    @Override
    @Transactional
    public boolean returnCoupon(Long memberCouponId) {
        UmsMemberCoupon memberCoupon = memberCouponMapper.selectById(memberCouponId);
        if (memberCoupon == null || memberCoupon.getStatus() != 1) {
            log.warn("优惠券无法返还, memberCouponId={}", memberCouponId);
            return false;
        }

        memberCoupon.setStatus(0);
        memberCoupon.setOrderId(null);
        memberCoupon.setUseTime(null);
        memberCouponMapper.updateById(memberCoupon);

        log.info("返还优惠券成功, memberCouponId={}", memberCouponId);
        return true;
    }
}
