package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.CouponParam;
import com.mall.admin.service.SmsCouponService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.SmsCouponMapper;
import com.mall.mbg.mapper.UmsMemberCouponMapper;
import com.mall.mbg.model.SmsCoupon;
import com.mall.mbg.model.UmsMemberCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SmsCouponServiceImpl implements SmsCouponService {

    private final SmsCouponMapper couponMapper;
    private final UmsMemberCouponMapper memberCouponMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CommonResult create(CouponParam param) {
        SmsCoupon coupon = SmsCoupon.builder()
                .name(param.getName())
                .type(param.getType())
                .amount(param.getAmount())
                .minPoint(param.getMinPoint())
                .maxDiscount(param.getMaxDiscount())
                .totalCount(param.getTotalCount())
                .remainCount(param.getTotalCount())
                .perLimit(param.getPerLimit())
                .startTime(parseDateTime(param.getStartTime()))
                .endTime(parseDateTime(param.getEndTime()))
                .useType(param.getUseType())
                .status(param.getStatus() != null ? param.getStatus() : 1)
                .createTime(LocalDateTime.now())
                .build();
        couponMapper.insert(coupon);
        return CommonResult.success(coupon);
    }

    @Override
    public CommonResult update(Long id, CouponParam param) {
        SmsCoupon existing = couponMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("优惠券不存在");
        }
        if (hasMemberReceived(id)) {
            return CommonResult.failed("已有用户领取该优惠券，无法修改");
        }
        SmsCoupon coupon = new SmsCoupon();
        coupon.setId(id);
        coupon.setName(param.getName());
        coupon.setType(param.getType());
        coupon.setAmount(param.getAmount());
        coupon.setMinPoint(param.getMinPoint());
        coupon.setMaxDiscount(param.getMaxDiscount());
        coupon.setTotalCount(param.getTotalCount());
        coupon.setPerLimit(param.getPerLimit());
        coupon.setStartTime(parseDateTime(param.getStartTime()));
        coupon.setEndTime(parseDateTime(param.getEndTime()));
        coupon.setUseType(param.getUseType());
        couponMapper.updateById(coupon);
        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult delete(Long id) {
        SmsCoupon existing = couponMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("优惠券不存在");
        }
        if (hasMemberReceived(id)) {
            return CommonResult.failed("已有用户领取该优惠券，无法删除");
        }
        couponMapper.deleteById(id);
        return CommonResult.success("删除成功");
    }

    @Override
    public CommonPage<SmsCoupon> list(String keyword, Integer type, Integer status, Integer pageNum, Integer pageSize) {
        Page<SmsCoupon> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SmsCoupon> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SmsCoupon::getName, keyword);
        }
        if (type != null) {
            wrapper.eq(SmsCoupon::getType, type);
        }
        if (status != null) {
            wrapper.eq(SmsCoupon::getStatus, status);
        }
        wrapper.orderByDesc(SmsCoupon::getCreateTime);
        Page<SmsCoupon> result = couponMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }

    @Override
    public SmsCoupon getById(Long id) {
        return couponMapper.selectById(id);
    }

    @Override
    public CommonResult updateStatus(Long id, Integer status) {
        SmsCoupon existing = couponMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("优惠券不存在");
        }
        SmsCoupon coupon = new SmsCoupon();
        coupon.setId(id);
        coupon.setStatus(status);
        couponMapper.updateById(coupon);
        return CommonResult.success("状态更新成功");
    }

    private boolean hasMemberReceived(Long couponId) {
        LambdaQueryWrapper<UmsMemberCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberCoupon::getCouponId, couponId);
        return memberCouponMapper.selectCount(wrapper) > 0;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (!StringUtils.hasText(dateTimeStr)) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, FORMATTER);
    }
}
