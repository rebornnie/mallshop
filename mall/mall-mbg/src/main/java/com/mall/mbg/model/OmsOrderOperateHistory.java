package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("oms_order_operate_history")
public class OmsOrderOperateHistory {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;

    private String operateMan;

    private Integer orderStatus;

    private String note;

    @TableField("create_time")
    private LocalDateTime createTime;
}
