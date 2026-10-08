package com.mall.portal.service;

import com.mall.common.api.CommonResult;
import com.mall.mbg.model.OmsCart;
import com.mall.portal.dto.CartAddParam;
import com.mall.portal.dto.CartUpdateParam;

import java.util.List;

public interface OmsCartPortalService {

    CommonResult add(CartAddParam param);

    List<OmsCart> list();

    CommonResult update(CartUpdateParam param);

    CommonResult delete(Long id);

    CommonResult clear();
}
