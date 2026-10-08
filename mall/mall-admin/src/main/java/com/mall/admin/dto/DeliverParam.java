package com.mall.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliverParam {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private String deliveryCompany;

    private String deliverySn;
}
