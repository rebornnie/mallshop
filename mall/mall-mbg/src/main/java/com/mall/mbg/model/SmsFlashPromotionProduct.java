package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sms_flash_promotion_product")
public class SmsFlashPromotionProduct {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long flashPromotionId;

    private Long productId;

    private Long skuId;

    private BigDecimal flashPrice;

    private Integer flashStock;

    private Integer flashSale;

    private Integer sort;

    private Integer status;
}
