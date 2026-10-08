package com.mall.portal.controller;

import com.mall.common.api.CommonResult;
import com.mall.portal.dto.PayParam;
import com.mall.portal.service.OmsPayPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oms/pay")
@RequiredArgsConstructor
@Tag(name = "支付")
public class OmsPayController {

    private final OmsPayPortalService payPortalService;

    @PostMapping("/alipay")
    @Operation(summary = "发起支付宝支付")
    public CommonResult alipay(@Valid @RequestBody PayParam param) {
        return payPortalService.alipay(param.getOrderId());
    }

    @PostMapping("/callback")
    @Operation(summary = "支付宝回调")
    public String callback(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueBuilder = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueBuilder.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueBuilder.toString());
        }
        return payPortalService.callback(params);
    }

    @GetMapping("/status")
    @Operation(summary = "查询支付状态")
    public CommonResult payStatus(@RequestParam Long orderId) {
        return payPortalService.payStatus(orderId);
    }
}
