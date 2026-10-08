package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sms_coupon")
public class SmsCoupon {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：1满减券 2折扣券 3无门槛券
     */
    private Integer type;

    /**
     * 优惠金额/折扣率
     * 满减券：优惠金额
     * 折扣券：折扣率（如0.8表示8折）
     * 无门槛券：优惠金额
     */
    private BigDecimal amount;

    /**
     * 使用门槛（满多少可用）
     */
    private BigDecimal minPoint;

    /**
     * 最大优惠金额（折扣券使用）
     */
    private BigDecimal maxDiscount;

    /**
     * 发行总量
     */
    private Integer totalCount;

    /**
     * 剩余数量
     */
    private Integer remainCount;

    /**
     * 每人限领数量
     */
    private Integer perLimit;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 使用范围：0全店 1指定分类 2指定商品
     */
    private Integer useType;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
