package com.mall.admin.controller;

import com.mall.admin.dto.CouponParam;
import com.mall.admin.service.SmsCouponService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.SmsCoupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coupon")
@RequiredArgsConstructor
@Tag(name = "优惠券管理", description = "优惠券CRUD及状态管理接口")
public class SmsCouponController {

    private final SmsCouponService couponService;

    @PostMapping("/create")
    @Operation(summary = "创建优惠券")
    public CommonResult create(@Valid @RequestBody CouponParam param) {
        return couponService.create(param);
    }

    @PutMapping("/update")
    @Operation(summary = "更新优惠券")
    public CommonResult update(@RequestParam Long id, @Valid @RequestBody CouponParam param) {
        return couponService.update(id, param);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除优惠券")
    public CommonResult delete(@PathVariable Long id) {
        return couponService.delete(id);
    }

    @GetMapping("/list")
    @Operation(summary = "获取优惠券列表")
    public CommonResult<CommonPage<SmsCoupon>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return CommonResult.success(couponService.list(keyword, type, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取优惠券详情")
    public CommonResult<SmsCoupon> getById(@PathVariable Long id) {
        SmsCoupon coupon = couponService.getById(id);
        if (coupon == null) {
            return CommonResult.failed("优惠券不存在");
        }
        return CommonResult.success(coupon);
    }

    @PutMapping("/updateStatus")
    @Operation(summary = "更新优惠券状态")
    public CommonResult updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        return couponService.updateStatus(id, status);
    }
}
