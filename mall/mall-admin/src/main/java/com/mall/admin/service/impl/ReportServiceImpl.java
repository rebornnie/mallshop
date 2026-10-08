package com.mall.admin.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.dto.OrderExportDTO;
import com.mall.admin.service.ReportService;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.OmsOrderItemMapper;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.OmsOrderItem;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;

    @Override
    public void exportOrder(String orderSn, Integer status, String receiverPhone, String startTime, String endTime, HttpServletResponse response) {
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderSn)) {
            wrapper.like(OmsOrder::getOrderSn, orderSn);
        }
        if (status != null) {
            wrapper.eq(OmsOrder::getStatus, status);
        }
        if (StringUtils.hasText(receiverPhone)) {
            wrapper.like(OmsOrder::getReceiverPhone, receiverPhone);
        }
        if (StringUtils.hasText(startTime)) {
            try {
                wrapper.ge(OmsOrder::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                log.warn("开始时间格式错误: {}", startTime);
            }
        }
        if (StringUtils.hasText(endTime)) {
            try {
                wrapper.le(OmsOrder::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                log.warn("结束时间格式错误: {}", endTime);
            }
        }
        wrapper.orderByDesc(OmsOrder::getCreateTime);
        List<OmsOrder> orderList = orderMapper.selectList(wrapper);

        List<OrderExportDTO> exportList = new ArrayList<>();
        for (OmsOrder order : orderList) {
            OrderExportDTO dto = new OrderExportDTO();
            dto.setOrderSn(order.getOrderSn());
            dto.setStatus(getStatusText(order.getStatus()));
            dto.setTotalAmount(order.getTotalAmount());
            dto.setPayAmount(order.getPayAmount());
            dto.setReceiverName(order.getReceiverName());
            dto.setReceiverPhone(order.getReceiverPhone());
            dto.setReceiverAddress(buildAddress(order));
            dto.setCreateTime(order.getCreateTime());
            dto.setPayTime(order.getPayTime());
            exportList.add(dto);
        }

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("订单导出_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), OrderExportDTO.class).sheet("订单列表").doWrite(exportList);
        } catch (IOException e) {
            log.error("订单导出失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    @Override
    public CommonResult<Map<String, Object>> salesReport(String startTime, String endTime) {
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(OmsOrder::getStatus, 1).le(OmsOrder::getStatus, 3);
        if (StringUtils.hasText(startTime)) {
            try {
                wrapper.ge(OmsOrder::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                return CommonResult.failed("开始时间格式错误");
            }
        }
        if (StringUtils.hasText(endTime)) {
            try {
                wrapper.le(OmsOrder::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                return CommonResult.failed("结束时间格式错误");
            }
        }
        List<OmsOrder> orderList = orderMapper.selectList(wrapper);

        int totalOrderCount = orderList.size();
        int paidOrderCount = 0;
        int deliveredOrderCount = 0;
        int completedOrderCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPayAmount = BigDecimal.ZERO;

        for (OmsOrder order : orderList) {
            totalAmount = totalAmount.add(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
            totalPayAmount = totalPayAmount.add(order.getPayAmount() != null ? order.getPayAmount() : BigDecimal.ZERO);
            if (order.getStatus() != null) {
                switch (order.getStatus()) {
                    case 1 -> paidOrderCount++;
                    case 2 -> deliveredOrderCount++;
                    case 3 -> completedOrderCount++;
                    default -> {}
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalOrderCount", totalOrderCount);
        result.put("paidOrderCount", paidOrderCount);
        result.put("deliveredOrderCount", deliveredOrderCount);
        result.put("completedOrderCount", completedOrderCount);
        result.put("totalAmount", totalAmount);
        result.put("totalPayAmount", totalPayAmount);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<Map<String, Object>> productSalesRank(String startTime, String endTime, Integer topN) {
        if (topN == null || topN <= 0) {
            topN = 10;
        }

        LambdaQueryWrapper<OmsOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.ge(OmsOrder::getStatus, 1).le(OmsOrder::getStatus, 3);
        if (StringUtils.hasText(startTime)) {
            try {
                orderWrapper.ge(OmsOrder::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                return CommonResult.failed("开始时间格式错误");
            }
        }
        if (StringUtils.hasText(endTime)) {
            try {
                orderWrapper.le(OmsOrder::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                return CommonResult.failed("结束时间格式错误");
            }
        }
        List<OmsOrder> orderList = orderMapper.selectList(orderWrapper);

        Map<Long, ProductSales> salesMap = new HashMap<>();
        for (OmsOrder order : orderList) {
            LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OmsOrderItem::getOrderId, order.getId());
            List<OmsOrderItem> items = orderItemMapper.selectList(itemWrapper);
            for (OmsOrderItem item : items) {
                Long productId = item.getProductId();
                if (productId == null) {
                    continue;
                }
                ProductSales sales = salesMap.computeIfAbsent(productId, k -> new ProductSales());
                sales.productId = productId;
                sales.productName = item.getProductName();
                sales.salesCount += item.getProductQuantity() != null ? item.getProductQuantity() : 0;
                sales.salesAmount = sales.salesAmount.add(
                        item.getProductPrice() != null && item.getProductQuantity() != null
                                ? item.getProductPrice().multiply(BigDecimal.valueOf(item.getProductQuantity()))
                                : BigDecimal.ZERO
                );
            }
        }

        List<ProductSales> rankList = new ArrayList<>(salesMap.values());
        rankList.sort((a, b) -> b.salesAmount.compareTo(a.salesAmount));
        if (rankList.size() > topN) {
            rankList = rankList.subList(0, topN);
        }

        List<Map<String, Object>> records = new ArrayList<>();
        for (ProductSales sales : rankList) {
            Map<String, Object> record = new HashMap<>();
            record.put("productId", sales.productId);
            record.put("productName", sales.productName);
            record.put("salesCount", sales.salesCount);
            record.put("salesAmount", sales.salesAmount);
            records.add(record);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("topN", topN);
        return CommonResult.success(result);
    }

    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            case 6 -> "已退款";
            default -> "未知";
        };
    }

    private String buildAddress(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(order.getReceiverProvince())) {
            sb.append(order.getReceiverProvince());
        }
        if (StringUtils.hasText(order.getReceiverCity())) {
            sb.append(order.getReceiverCity());
        }
        if (StringUtils.hasText(order.getReceiverDistrict())) {
            sb.append(order.getReceiverDistrict());
        }
        if (StringUtils.hasText(order.getReceiverDetailAddress())) {
            sb.append(order.getReceiverDetailAddress());
        }
        return sb.toString();
    }

    private static class ProductSales {
        Long productId;
        String productName;
        int salesCount;
        BigDecimal salesAmount = BigDecimal.ZERO;
    }
}
