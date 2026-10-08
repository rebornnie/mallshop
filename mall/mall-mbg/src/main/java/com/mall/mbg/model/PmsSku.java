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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("pms_sku")
public class PmsSku {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long productId;

    private String skuCode;

    private BigDecimal price;

    private String spData;

    private String pic;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private Integer stock;
}
