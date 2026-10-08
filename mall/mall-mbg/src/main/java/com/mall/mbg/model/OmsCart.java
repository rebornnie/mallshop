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
@TableName("oms_cart")
public class OmsCart {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long memberId;

    private Long productId;

    private Long skuId;

    private Integer quantity;

    private BigDecimal price;

    private String productName;

    private String productPic;

    private String spData;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Integer productStatus;

    @TableField(exist = false)
    private Integer stock;
}
