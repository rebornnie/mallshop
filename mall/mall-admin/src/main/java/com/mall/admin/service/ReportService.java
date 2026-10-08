package com.mall.admin.service;

import com.mall.common.api.CommonResult;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface ReportService {

    void exportOrder(String orderSn, Integer status, String receiverPhone, String startTime, String endTime, HttpServletResponse response);

    CommonResult<Map<String, Object>> salesReport(String startTime, String endTime);

    CommonResult<Map<String, Object>> productSalesRank(String startTime, String endTime, Integer topN);
}
