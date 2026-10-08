package com.mall.portal.controller;

import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.OmsOrder;
import com.mall.portal.dto.CancelOrderParam;
import com.mall.portal.dto.OrderCreateParam;
import com.mall.portal.service.OmsOrderPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("portalOmsOrderController")
@RequestMapping("/api/oms/order")
@RequiredArgsConstructor
@Tag(name = "前台订单")
public class OmsOrderController {

    private final OmsOrderPortalService orderPortalService;

    @PostMapping("/generateConfirm")
    @Operation(summary = "生成确认订单信息")
    public CommonResult generateConfirm(@RequestBody Map<String, List<Long>> param) {
        List<Long> cartIds = param.get("cartIds");
        if (cartIds == null || cartIds.isEmpty()) {
            return CommonResult.failed("购物车ID不能为空");
        }
        return orderPortalService.generateConfirm(cartIds);
    }

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public CommonResult create(@Valid @RequestBody OrderCreateParam param) {
        return orderPortalService.create(param);
    }

    @GetMapping("/list")
    @Operation(summary = "获取订单列表")
    public CommonResult list(@RequestParam(required = false) Integer status,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        CommonPage<OmsOrder> orderPage = orderPortalService.list(status, pageNum, pageSize);
        return CommonResult.success(orderPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public CommonResult detail(@PathVariable Long id) {
        OmsOrder order = orderPortalService.detail(id);
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }
        return CommonResult.success(order);
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消订单")
    public CommonResult cancel(@PathVariable Long id, @RequestBody(required = false) CancelOrderParam param) {
        String reason = param != null ? param.getReason() : null;
        return orderPortalService.cancel(id, reason);
    }

    @PutMapping("/confirmReceive/{id}")
    @Operation(summary = "确认收货")
    public CommonResult confirmReceive(@PathVariable Long id) {
        return orderPortalService.confirmReceive(id);
    }
}
