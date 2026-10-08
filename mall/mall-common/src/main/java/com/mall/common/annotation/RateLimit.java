package com.mall.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 *
 * 基于Redis令牌桶算法实现，支持按IP或用户ID限流。
 *
 * 使用示例：
 * <pre>
 *   @RateLimit(key = "login", limit = 5, window = 60)
 *   public CommonResult login(...) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流标识，用于区分不同接口
     */
    String key();

    /**
     * 时间窗口内允许的最大请求次数
     */
    int limit() default 100;

    /**
     * 时间窗口大小，单位：秒
     */
    int window() default 60;

    /**
     * 限流维度
     * IP - 按客户端IP限流
     * USER - 按用户ID限流（需登录）
     * GLOBAL - 全局限流
     */
    LimitType limitType() default LimitType.IP;

    /**
     * 被限流时的提示消息
     */
    String message() default "请求过于频繁，请稍后再试";

    enum LimitType {
        IP, USER, GLOBAL
    }
}
