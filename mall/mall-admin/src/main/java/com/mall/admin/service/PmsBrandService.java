package com.mall.admin.service;

import com.mall.admin.dto.BrandParam;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsBrand;

public interface PmsBrandService {

    CommonResult create(BrandParam param);

    CommonResult update(Long id, BrandParam param);

    CommonResult delete(Long id);

    CommonPage<PmsBrand> list(String keyword, Integer pageNum, Integer pageSize);
}
