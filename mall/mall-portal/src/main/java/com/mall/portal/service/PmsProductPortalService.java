package com.mall.portal.service;

import com.mall.common.api.CommonPage;
import com.mall.mbg.model.PmsBrand;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.PmsProductCategory;

import java.util.List;

public interface PmsProductPortalService {

    CommonPage<PmsProduct> list(Long categoryId, Long brandId, String keyword, String sort, Integer pageNum, Integer pageSize);

    PmsProduct detail(Long id);

    List<PmsProductCategory> categoryList();

    CommonPage<PmsBrand> brandList(Integer pageNum, Integer pageSize);
}
