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
@TableName("oms_order_item")
public class OmsOrderItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;

    private String orderSn;

    private Long productId;

    private String productPic;

    private String productName;

    private String productSn;

    private BigDecimal productPrice;

    private Integer productQuantity;

    private Long skuId;

    private String spData;
}
