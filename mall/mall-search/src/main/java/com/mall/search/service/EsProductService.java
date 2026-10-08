package com.mall.search.service;

import com.mall.common.api.CommonPage;
import com.mall.search.domain.EsProduct;

public interface EsProductService {

    int importAll();

    CommonPage<EsProduct> search(String keyword, Long categoryId, Long brandId, Integer pageNum, Integer pageSize, String sort);
}
