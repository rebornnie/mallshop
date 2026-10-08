package com.mall.admin.service;

import com.mall.admin.dto.DeliverParam;
import com.mall.admin.dto.RefundParam;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.OmsOrder;

public interface OmsOrderService {

    CommonPage<OmsOrder> list(String orderSn, Integer status, String receiverPhone, String startTime, String endTime, Integer pageNum, Integer pageSize);

    OmsOrder getById(Long id);

    CommonResult deliver(DeliverParam param);

    CommonResult refund(RefundParam param);
}
