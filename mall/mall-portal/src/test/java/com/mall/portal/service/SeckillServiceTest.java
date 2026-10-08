package com.mall.portal.service;

import com.mall.portal.service.impl.SeckillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 秒杀服务测试类 - TDD模式（集成测试，使用真实Redis）
 *
 * 运行前需确保本地Redis服务已启动（默认端口6379）
 */
class SeckillServiceTest {

    private StringRedisTemplate redisTemplate;
    private SeckillServiceImpl seckillService;

    private static final Long FLASH_ID = 1L;
    private static final Long SKU_ID = 10001L;
    private static final Long MEMBER_ID = 20001L;
    private static final String STOCK_KEY = "seckill:stock:1:10001";
    private static final String USER_KEY = "seckill:user:1:20001";

    @BeforeEach
    void setUp() {
        LettuceConnectionFactory connectionFactory =
                new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
        connectionFactory.afterPropertiesSet();

        redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();

        seckillService = new SeckillServiceImpl(redisTemplate);

        // 清理测试数据
        redisTemplate.delete(STOCK_KEY);
        redisTemplate.delete(USER_KEY);
        redisTemplate.delete("seckill:result:1:20001");
    }

    @Test
    @DisplayName("TC001: 正常秒杀 - 库存充足且未参与过，应成功")
    void seckill_WhenStockSufficientAndNotParticipated_ShouldSuccess() {
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, 100);
        boolean result = seckillService.seckill(FLASH_ID, SKU_ID, MEMBER_ID);
        assertTrue(result, "正常秒杀应成功");
        assertEquals(99L, seckillService.getSeckillStock(FLASH_ID, SKU_ID), "剩余库存应为99");
    }

    @Test
    @DisplayName("TC002: 库存不足 - 秒杀应失败")
    void seckill_WhenStockInsufficient_ShouldFail() {
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, 0);
        boolean result = seckillService.seckill(FLASH_ID, SKU_ID, MEMBER_ID);
        assertFalse(result, "库存不足时秒杀应失败");
    }

    @Test
    @DisplayName("TC003: 重复秒杀 - 同一用户应失败")
    void seckill_WhenAlreadyParticipated_ShouldFail() {
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, 100);
        seckillService.seckill(FLASH_ID, SKU_ID, MEMBER_ID);
        boolean result = seckillService.seckill(FLASH_ID, SKU_ID, MEMBER_ID);
        assertFalse(result, "重复秒杀应失败");
    }

    @Test
    @DisplayName("TC004: 并发秒杀 - 100线程抢50库存，不超卖")
    void seckill_ConcurrentSeckill_ShouldNotOversell() throws InterruptedException {
        int initialStock = 50;
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, initialStock);

        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(20);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final Long memberId = 30000L + i;
            executor.submit(() -> {
                try {
                    if (seckillService.seckill(FLASH_ID, SKU_ID, memberId)) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Long remaining = seckillService.getSeckillStock(FLASH_ID, SKU_ID);
        assertEquals(initialStock, successCount.get() + remaining,
                "成功秒杀数 + 剩余库存应等于初始库存");
        assertTrue(successCount.get() <= initialStock,
                "成功秒杀次数不应超过初始库存");
        assertTrue(remaining >= 0,
                "剩余库存不应为负数");
    }

    @Test
    @DisplayName("TC005: 初始化秒杀库存 - 应正确设置Redis")
    void initSeckillStock_ShouldSetCorrectValue() {
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, 100);
        assertEquals(100L, seckillService.getSeckillStock(FLASH_ID, SKU_ID), "库存应为100");
    }

    @Test
    @DisplayName("TC006: 检查用户是否已秒杀 - 已参与应返回true")
    void hasParticipated_WhenParticipated_ShouldReturnTrue() {
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, 100);
        seckillService.seckill(FLASH_ID, SKU_ID, MEMBER_ID);
        assertTrue(seckillService.hasParticipated(FLASH_ID, MEMBER_ID));
    }

    @Test
    @DisplayName("TC007: 检查用户是否已秒杀 - 未参与应返回false")
    void hasParticipated_WhenNotParticipated_ShouldReturnFalse() {
        assertFalse(seckillService.hasParticipated(FLASH_ID, MEMBER_ID));
    }

    @Test
    @DisplayName("TC008: 获取秒杀库存 - 应返回正确数量")
    void getSeckillStock_ShouldReturnCorrectValue() {
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, 30);
        assertEquals(30L, seckillService.getSeckillStock(FLASH_ID, SKU_ID));
    }

    @Test
    @DisplayName("TC009: 获取秒杀库存 - 不存在应返回0")
    void getSeckillStock_WhenNotExist_ShouldReturnZero() {
        assertEquals(0L, seckillService.getSeckillStock(FLASH_ID, SKU_ID));
    }

    @Test
    @DisplayName("TC010: 回滚秒杀库存 - 应正确返还")
    void rollbackSeckillStock_ShouldRestoreStock() {
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, 50);
        seckillService.seckill(FLASH_ID, SKU_ID, MEMBER_ID);
        boolean result = seckillService.rollbackSeckillStock(FLASH_ID, SKU_ID, 1);
        assertTrue(result, "回滚应成功");
        assertEquals(50L, seckillService.getSeckillStock(FLASH_ID, SKU_ID), "库存应恢复为50");
    }

    @Test
    @DisplayName("TC011: 清除用户秒杀标记 - 应成功清除")
    void clearUserSeckillMark_ShouldClearMark() {
        seckillService.initSeckillStock(FLASH_ID, SKU_ID, 100);
        seckillService.seckill(FLASH_ID, SKU_ID, MEMBER_ID);
        assertTrue(seckillService.hasParticipated(FLASH_ID, MEMBER_ID));
        seckillService.clearUserSeckillMark(FLASH_ID, MEMBER_ID);
        assertFalse(seckillService.hasParticipated(FLASH_ID, MEMBER_ID));
    }
}
