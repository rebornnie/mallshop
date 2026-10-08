package com.mall.admin.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuStockParam {

    private Long id;

    private String skuCode;

    private BigDecimal price;

    private String spData;

    private String pic;

    private Integer stock;
}
