package com.mall.portal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateParam {

    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    @NotEmpty(message = "购物车ID不能为空")
    private List<Long> cartIds;

    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    private String note;

    private Long memberCouponId;
}
