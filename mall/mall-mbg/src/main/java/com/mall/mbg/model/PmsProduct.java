package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("pms_product")
public class PmsProduct {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long categoryId;

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

    private Integer sale;

    private String unit;

    private String description;

    private String albumPics;

    private String detailTitle;

    private String detailDesc;

    private Integer publishStatus;

    private Integer newStatus;

    private Integer recommendStatus;

    private Integer sort;

    @TableField(exist = false)
    private String brandName;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private List<PmsSku> skuList;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
