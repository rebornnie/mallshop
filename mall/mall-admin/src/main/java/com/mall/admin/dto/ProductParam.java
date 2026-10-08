package com.mall.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductParam {

    private Long id;

    private Long categoryId;

    private List<Long> categoryIds;

    private Long brandId;

    private String name;

    private String subtitle;

    private String productSn;

    private String pic;

    private String pics;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private BigDecimal costPrice;

    private Integer stock;

    private String unit;

    private String description;

    private Integer publishStatus;

    private Integer newStatus;

    private Integer recommendStatus;

    private Integer sort;

    private List<SkuStockParam> skuList;
}
