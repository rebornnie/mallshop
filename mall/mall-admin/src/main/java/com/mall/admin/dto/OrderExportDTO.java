package com.mall.admin.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderExportDTO {

    @ExcelProperty("订单编号")
    private String orderSn;

    @ExcelProperty("订单状态")
    private String status;

    @ExcelProperty("订单金额")
    private BigDecimal totalAmount;

    @ExcelProperty("实付金额")
    private BigDecimal payAmount;

    @ExcelProperty("收货人")
    private String receiverName;

    @ExcelProperty("收货电话")
    private String receiverPhone;

    @ExcelProperty("收货地址")
    private String receiverAddress;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("支付时间")
    private LocalDateTime payTime;
}
