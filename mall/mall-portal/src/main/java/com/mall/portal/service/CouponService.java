package com.mall.portal.service;

import com.mall.mbg.model.SmsCoupon;
import com.mall.mbg.model.UmsMemberCoupon;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口
 *
 * 支持三种优惠券类型：
 * 1. 满减券：订单金额 >= 门槛金额，减免固定金额
 * 2. 折扣券：订单金额 * 折扣率，有最大优惠上限
 * 3. 无门槛券：直接减免固定金额
 */
public interface CouponService {

    /**
     * 领取优惠券
     *
     * @param couponId 优惠券ID
     * @param memberId 会员ID
     * @return true-领取成功, false-领取失败
     */
    boolean receiveCoupon(Long couponId, Long memberId);

    /**
     * 获取可领取优惠券列表
     *
     * @return 优惠券列表
     */
    List<SmsCoupon> getAvailableCoupons();

    /**
     * 获取我的优惠券列表
     *
     * @param memberId 会员ID
     * @param status 状态 0未使用 1已使用 2已过期
     * @return 用户优惠券列表
     */
    List<UmsMemberCoupon> getMyCoupons(Long memberId, Integer status);

    /**
     * 计算优惠券优惠金额
     *
     * @param couponId 优惠券ID
     * @param orderAmount 订单金额
     * @return 优惠金额，不满足条件返回0
     */
    BigDecimal calculateDiscount(Long couponId, BigDecimal orderAmount);

    /**
     * 选择最优优惠券
     *
     * @param couponIds 优惠券ID列表
     * @param orderAmount 订单金额
     * @return 最优优惠券ID
     */
    Long selectBestCoupon(List<Long> couponIds, BigDecimal orderAmount);

    /**
     * 使用优惠券
     *
     * @param memberCouponId 用户优惠券ID
     * @param orderId 订单ID
     * @return true-使用成功
     */
    boolean useCoupon(Long memberCouponId, Long orderId);

    /**
     * 返还优惠券（订单取消时）
     *
     * @param memberCouponId 用户优惠券ID
     * @return true-返还成功
     */
    boolean returnCoupon(Long memberCouponId);
}
