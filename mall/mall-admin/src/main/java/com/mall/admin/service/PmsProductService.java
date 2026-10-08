package com.mall.admin.service;

import com.mall.admin.dto.ProductParam;
import com.mall.admin.dto.UpdatePublishStatusParam;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsProduct;

import java.util.List;

public interface PmsProductService {

    CommonResult create(ProductParam param);

    CommonResult update(Long id, ProductParam param);

    CommonResult delete(Long id);

    CommonPage<PmsProduct> list(String keyword, Long categoryId, Long brandId, Integer publishStatus, Integer pageNum, Integer pageSize);

    PmsProduct getById(Long id);

    List<Long> getCategoryIds(Long productId);

    CommonResult updatePublishStatus(UpdatePublishStatusParam param);

    CommonResult batchDelete(List<Long> ids);
}
