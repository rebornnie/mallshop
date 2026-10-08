package com.mall.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value, long timeout) {
        if (timeout > 0) {
            stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            return;
        }
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public Boolean expire(String key, long timeout) {
        return stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }
}
