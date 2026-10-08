package com.mall.admin.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponParam {

    private Long id;

    private String name;

    private Integer type;

    private BigDecimal amount;

    private BigDecimal minPoint;

    private BigDecimal maxDiscount;

    private Integer totalCount;

    private Integer perLimit;

    private String startTime;

    private String endTime;

    private Integer useType;

    private Integer status;
}
