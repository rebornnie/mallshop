package com.mall.admin.controller;

import com.mall.admin.service.StatService;
import com.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/stat")
@RequiredArgsConstructor
@Tag(name = "统计管理", description = "订单、商品、用户统计接口")
public class StatController {

    private final StatService statService;

    @GetMapping("/order")
    @Operation(summary = "订单统计")
    public CommonResult<Map<String, Object>> orderStat(@RequestParam(defaultValue = "week") String type) {
        return CommonResult.success(statService.orderStat(type));
    }

    @GetMapping("/product")
    @Operation(summary = "商品统计")
    public CommonResult<Map<String, Object>> productStat() {
        return CommonResult.success(statService.productStat());
    }

    @GetMapping("/user")
    @Operation(summary = "用户统计")
    public CommonResult<Map<String, Object>> userStat() {
        return CommonResult.success(statService.userStat());
    }
}
