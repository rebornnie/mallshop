package com.mall.portal.service;

import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.OmsOrder;
import com.mall.portal.dto.OrderCreateParam;

import java.util.List;
import java.util.Map;

public interface OmsOrderPortalService {

    CommonResult generateConfirm(List<Long> cartIds);

    CommonResult<Map<String, Object>> create(OrderCreateParam param);

    CommonPage<OmsOrder> list(Integer status, Integer pageNum, Integer pageSize);

    OmsOrder detail(Long id);

    CommonResult cancel(Long id, String reason);

    CommonResult confirmReceive(Long id);
}
