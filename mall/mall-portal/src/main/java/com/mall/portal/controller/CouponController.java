package com.mall.portal.controller;

import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.SmsCouponMapper;
import com.mall.mbg.mapper.UmsMemberCouponMapper;
import com.mall.mbg.model.SmsCoupon;
import com.mall.mbg.model.UmsMember;
import com.mall.mbg.model.UmsMemberCoupon;
import com.mall.portal.service.CouponService;
import com.mall.portal.service.UmsMemberPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oms/coupon")
@RequiredArgsConstructor
@Tag(name = "优惠券")
public class CouponController {

    private final CouponService couponService;
    private final UmsMemberPortalService memberPortalService;
    private final SmsCouponMapper couponMapper;
    private final UmsMemberCouponMapper memberCouponMapper;

    @GetMapping("/available")
    @Operation(summary = "获取可领取优惠券列表")
    public CommonResult<List<SmsCoupon>> getAvailableCoupons() {
        return CommonResult.success(couponService.getAvailableCoupons());
    }

    @PostMapping("/receive/{couponId}")
    @Operation(summary = "领取优惠券")
    public CommonResult<String> receiveCoupon(@PathVariable Long couponId) {
        UmsMember member = memberPortalService.getCurrentMember();
        if (member == null) {
            return CommonResult.failed("用户未登录");
        }
        boolean result = couponService.receiveCoupon(couponId, member.getId());
        return result ? CommonResult.success("领取成功") : CommonResult.failed("领取失败");
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的优惠券列表")
    public CommonResult<List<Map<String, Object>>> getMyCoupons(@RequestParam(required = false) Integer status) {
        UmsMember member = memberPortalService.getCurrentMember();
        if (member == null) {
            return CommonResult.failed("用户未登录");
        }
        List<UmsMemberCoupon> memberCoupons = couponService.getMyCoupons(member.getId(), status);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (UmsMemberCoupon memberCoupon : memberCoupons) {
            SmsCoupon coupon = couponMapper.selectById(memberCoupon.getCouponId());
            Map<String, Object> item = new HashMap<>();
            item.put("memberCoupon", memberCoupon);
            item.put("coupon", coupon);
            resultList.add(item);
        }
        return CommonResult.success(resultList);
    }

    @GetMapping("/availableForOrder")
    @Operation(summary = "获取当前订单可用优惠券")
    public CommonResult<List<Map<String, Object>>> getAvailableForOrder(@RequestParam BigDecimal orderAmount) {
        UmsMember member = memberPortalService.getCurrentMember();
        if (member == null) {
            return CommonResult.failed("用户未登录");
        }
        List<UmsMemberCoupon> unusedCoupons = couponService.getMyCoupons(member.getId(), 0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (UmsMemberCoupon memberCoupon : unusedCoupons) {
            BigDecimal discount = couponService.calculateDiscount(memberCoupon.getCouponId(), orderAmount);
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                SmsCoupon coupon = couponMapper.selectById(memberCoupon.getCouponId());
                Map<String, Object> item = new HashMap<>();
                item.put("memberCoupon", memberCoupon);
                item.put("coupon", coupon);
                item.put("discount", discount);
                resultList.add(item);
            }
        }
        return CommonResult.success(resultList);
    }
}
