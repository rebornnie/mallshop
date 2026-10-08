package com.mall.portal.service;

import com.mall.common.api.CommonResult;

import java.util.Map;

public interface OmsPayPortalService {

    CommonResult<Map<String, String>> alipay(Long orderId);

    String callback(Map<String, String> params);

    CommonResult<Map<String, Object>> payStatus(Long orderId);
}
