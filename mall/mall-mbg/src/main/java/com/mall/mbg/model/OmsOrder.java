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
@TableName("oms_order")
public class OmsOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String orderSn;

    private Long memberId;

    private BigDecimal totalAmount;

    private BigDecimal freightAmount;

    private BigDecimal payAmount;

    private Integer payType;

    private Integer status;

    private String receiverName;

    private String receiverPhone;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverDetailAddress;

    private String note;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @TableField("close_time")
    private LocalDateTime closeTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    private Integer deleteStatus;

    @TableField(exist = false)
    private List<OmsOrderItem> orderItemList;
}
