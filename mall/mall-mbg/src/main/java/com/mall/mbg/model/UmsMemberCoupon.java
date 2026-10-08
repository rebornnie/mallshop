package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户优惠券表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ums_member_coupon")
public class UmsMemberCoupon {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 使用订单ID
     */
    private Long orderId;

    /**
     * 状态：0未使用 1已使用 2已过期
     */
    private Integer status;

    /**
     * 领取时间
     */
    private LocalDateTime getTime;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
