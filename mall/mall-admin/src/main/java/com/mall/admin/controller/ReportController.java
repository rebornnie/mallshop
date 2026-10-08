package com.mall.admin.controller;

import com.mall.admin.service.ReportService;
import com.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/report")
@RequiredArgsConstructor
@Tag(name = "数据报表", description = "订单导出、销售报表、商品排行")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/exportOrder")
    @Operation(summary = "订单导出Excel")
    public void exportOrder(
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String receiverPhone,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) {
        reportService.exportOrder(orderSn, status, receiverPhone, startTime, endTime, response);
    }

    @GetMapping("/sales")
    @Operation(summary = "销售报表统计")
    public CommonResult<Map<String, Object>> salesReport(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.salesReport(startTime, endTime);
    }

    @GetMapping("/productRank")
    @Operation(summary = "商品销售排行")
    public CommonResult<Map<String, Object>> productSalesRank(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "10") Integer topN) {
        return reportService.productSalesRank(startTime, endTime, topN);
    }
}
