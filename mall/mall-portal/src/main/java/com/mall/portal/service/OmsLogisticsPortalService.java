package com.mall.portal.service;

import com.mall.common.api.CommonResult;

public interface OmsLogisticsPortalService {

    CommonResult getLogisticsByOrderId(Long orderId);
}
