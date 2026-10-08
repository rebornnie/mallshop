package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("oms_order_logistics")
public class OmsOrderLogistics {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String deliveryCompany;

    private String deliverySn;

    private Integer status;

    private String detail;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
