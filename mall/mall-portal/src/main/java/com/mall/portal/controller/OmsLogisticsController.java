package com.mall.portal.controller;

import com.mall.common.api.CommonResult;
import com.mall.portal.service.OmsLogisticsPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oms/logistics")
@RequiredArgsConstructor
@Tag(name = "物流查询")
public class OmsLogisticsController {

    private final OmsLogisticsPortalService logisticsPortalService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "查询订单物流")
    public CommonResult getLogisticsByOrderId(@PathVariable Long orderId) {
        return logisticsPortalService.getLogisticsByOrderId(orderId);
    }
}
