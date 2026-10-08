package com.mall.admin.controller;

import com.mall.admin.dto.DeliverParam;
import com.mall.admin.dto.RefundParam;
import com.mall.admin.service.OmsOrderService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.OmsOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("adminOmsOrderController")
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单查询、发货、退款接口")
public class OmsOrderController {

    private final OmsOrderService orderService;

    @GetMapping("/list")
    @Operation(summary = "获取订单列表")
    public CommonResult<CommonPage<OmsOrder>> list(
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String receiverPhone,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(orderService.list(orderSn, status, receiverPhone, startTime, endTime, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public CommonResult<OmsOrder> getById(@PathVariable Long id) {
        OmsOrder order = orderService.getById(id);
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }
        return CommonResult.success(order);
    }

    @PostMapping("/deliver")
    @Operation(summary = "订单发货")
    public CommonResult deliver(@Valid @RequestBody DeliverParam param) {
        return orderService.deliver(param);
    }

    @PostMapping("/refund")
    @Operation(summary = "订单退款审批")
    public CommonResult refund(@Valid @RequestBody RefundParam param) {
        return orderService.refund(param);
    }
}
