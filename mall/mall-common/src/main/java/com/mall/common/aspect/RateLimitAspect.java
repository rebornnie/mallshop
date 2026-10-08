package com.mall.common.aspect;

import com.mall.common.annotation.RateLimit;
import com.mall.common.api.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 接口限流AOP切面
 *
 * 基于Redis滑动窗口计数实现限流。
 * 核心逻辑：
 * 1. 使用Redis Sorted Set存储请求时间戳
 * 2. 每次请求时移除窗口外的旧记录
 * 3. 统计窗口内记录数，超过limit则拒绝
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    /**
     * 限流Lua脚本（滑动窗口）
     *
     * KEYS[1] - Redis Key
     * ARGV[1] - 当前时间戳（毫秒）
     * ARGV[2] - 窗口起始时间戳（毫秒）
     * ARGV[3] - 最大请求次数
     *
     * 返回值：
     *   1  - 允许通过
     *   0  - 拒绝请求
     */
    private static final String RATE_LIMIT_LUA =
            "local key = KEYS[1] " +
            "local now = tonumber(ARGV[1]) " +
            "local windowStart = tonumber(ARGV[2]) " +
            "local limit = tonumber(ARGV[3]) " +
            "redis.call('ZREMRANGEBYSCORE', key, 0, windowStart) " +
            "local current = redis.call('ZCARD', key) " +
            "if current >= limit then " +
            "    return 0 " +
            "end " +
            "redis.call('ZADD', key, now, now) " +
            "redis.call('EXPIRE', key, math.ceil((now - windowStart) / 1000)) " +
            "return 1";

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>();

    static {
        RATE_LIMIT_SCRIPT.setScriptText(RATE_LIMIT_LUA);
        RATE_LIMIT_SCRIPT.setResultType(Long.class);
    }

    @Around("@annotation(com.mall.common.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String key = buildKey(rateLimit);
        if (key == null) {
            return point.proceed();
        }

        long now = System.currentTimeMillis();
        long windowStart = now - rateLimit.window() * 1000L;

        List<String> keys = Collections.singletonList("rate_limit:" + key + ":" + rateLimit.key());
        List<String> args = List.of(
                String.valueOf(now),
                String.valueOf(windowStart),
                String.valueOf(rateLimit.limit())
        );

        Long result;
        try {
            result = redisTemplate.execute(RATE_LIMIT_SCRIPT, keys, args.toArray());
        } catch (Exception e) {
            log.error("限流Redis执行异常, key={}", key, e);
            return point.proceed();
        }

        if (result == null || result == 0) {
            log.warn("接口限流触发, key={}, method={}.{}", key,
                    signature.getDeclaringTypeName(), method.getName());
            return CommonResult.failed(rateLimit.message());
        }

        return point.proceed();
    }

    private String buildKey(RateLimit rateLimit) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }

        switch (rateLimit.limitType()) {
            case IP -> {
                String ip = getClientIp(request);
                return ip;
            }
            case USER -> {
                String userId = getCurrentUserId(request);
                if (userId == null) {
                    return getClientIp(request);
                }
                return userId;
            }
            case GLOBAL -> {
                return "global";
            }
            default -> {
                return getClientIp(request);
            }
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getCurrentUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        Object attribute = request.getAttribute("userId");
        if (attribute != null) {
            return attribute.toString();
        }
        return null;
    }
}
