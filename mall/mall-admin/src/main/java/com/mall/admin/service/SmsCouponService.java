package com.mall.admin.service;

import com.mall.admin.dto.CouponParam;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.SmsCoupon;

public interface SmsCouponService {

    CommonResult create(CouponParam param);

    CommonResult update(Long id, CouponParam param);

    CommonResult delete(Long id);

    CommonPage<SmsCoupon> list(String keyword, Integer type, Integer status, Integer pageNum, Integer pageSize);

    SmsCoupon getById(Long id);

    CommonResult updateStatus(Long id, Integer status);
}
