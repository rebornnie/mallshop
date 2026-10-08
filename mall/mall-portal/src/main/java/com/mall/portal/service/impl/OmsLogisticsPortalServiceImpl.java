package com.mall.portal.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.OmsOrderLogisticsMapper;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.OmsOrderLogistics;
import com.mall.portal.service.OmsLogisticsPortalService;
import com.mall.portal.service.UmsMemberPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OmsLogisticsPortalServiceImpl implements OmsLogisticsPortalService {

    private final OmsOrderLogisticsMapper logisticsMapper;
    private final OmsOrderMapper orderMapper;
    private final UmsMemberPortalService memberPortalService;

    @Override
    public CommonResult getLogisticsByOrderId(Long orderId) {
        OmsOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }

        Long memberId = memberPortalService.getCurrentMember().getId();
        if (!order.getMemberId().equals(memberId)) {
            return CommonResult.failed("无权查看");
        }

        OmsOrderLogistics logistics = logisticsMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OmsOrderLogistics>()
                        .eq(OmsOrderLogistics::getOrderId, orderId)
        );

        if (logistics == null) {
            return CommonResult.failed("暂无物流信息");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderSn", order.getOrderSn());
        result.put("deliveryCompany", logistics.getDeliveryCompany());
        result.put("deliverySn", logistics.getDeliverySn());
        result.put("status", logistics.getStatus());

        List<Map<String, String>> detailList = null;
        if (logistics.getDetail() != null && !logistics.getDetail().isEmpty()) {
            detailList = JSON.parseObject(logistics.getDetail(), new TypeReference<List<Map<String, String>>>() {});
        }
        result.put("detailList", detailList);

        return CommonResult.success(result);
    }
}
