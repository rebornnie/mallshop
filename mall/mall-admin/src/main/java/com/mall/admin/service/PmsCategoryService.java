package com.mall.admin.service;

import com.mall.admin.dto.CategoryParam;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsProductCategory;

import java.util.List;

public interface PmsCategoryService {

    CommonResult create(CategoryParam param);

    CommonResult update(Long id, CategoryParam param);

    CommonResult delete(Long id);

    List<PmsProductCategory> list();
}
