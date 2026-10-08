package com.mall.portal.service;

import com.mall.portal.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 库存服务测试类 - TDD模式（集成测试，使用真实Redis）
 *
 * 运行前需确保本地Redis服务已启动（默认端口6379，database 1）
 *
 * 测试场景：
 * 1. 正常扣减库存
 * 2. 库存不足时扣减失败
 * 3. 并发场景下不超卖
 * 4. 库存回滚（取消订单）
 * 5. 重复扣减同一库存（幂等性）
 */
class StockServiceTest {

    private StringRedisTemplate redisTemplate;
    private StockServiceImpl stockService;

    private static final Long TEST_SKU_ID = 10001L;
    private static final String TEST_SKU_KEY = "stock:sku:10001";

    @BeforeEach
    void setUp() {
        // 手动创建Redis连接
        org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory connectionFactory =
                new org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory(
                        new org.springframework.data.redis.connection.RedisStandaloneConfiguration("localhost", 6379)
                );
        connectionFactory.afterPropertiesSet();

        redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();

        stockService = new StockServiceImpl(redisTemplate);

        // 清理测试数据
        redisTemplate.delete(TEST_SKU_KEY);
    }

    @Test
    @DisplayName("TC001: 正常扣减库存 - 库存充足时应成功扣减")
    void deductStock_WhenStockSufficient_ShouldSuccess() {
        stockService.initStock(TEST_SKU_ID, 10);
        boolean result = stockService.deductStock(TEST_SKU_ID, 3);
        assertTrue(result, "库存充足时扣减应成功");
        assertEquals(7L, stockService.getStock(TEST_SKU_ID), "剩余库存应为7");
    }

    @Test
    @DisplayName("TC002: 库存不足 - 扣减数量大于库存时应失败")
    void deductStock_WhenStockInsufficient_ShouldFail() {
        stockService.initStock(TEST_SKU_ID, 5);
        boolean result = stockService.deductStock(TEST_SKU_ID, 10);
        assertFalse(result, "库存不足时扣减应失败");
        assertEquals(5L, stockService.getStock(TEST_SKU_ID), "库存应保持不变");
    }

    @Test
    @DisplayName("TC003: 库存刚好扣完 - 扣减数量等于库存时应成功")
    void deductStock_WhenStockExact_ShouldSuccess() {
        stockService.initStock(TEST_SKU_ID, 5);
        boolean result = stockService.deductStock(TEST_SKU_ID, 5);
        assertTrue(result, "刚好扣完时应成功");
        assertEquals(0L, stockService.getStock(TEST_SKU_ID), "库存应为0");
    }

    @Test
    @DisplayName("TC004: 并发扣减 - 100个线程同时扣减，总扣减量不超过库存")
    void deductStock_ConcurrentDeduction_ShouldNotOversell() throws InterruptedException {
        int initialStock = 50;
        stockService.initStock(TEST_SKU_ID, initialStock);

        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(20);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    if (stockService.deductStock(TEST_SKU_ID, 1)) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Long remaining = stockService.getStock(TEST_SKU_ID);
        assertEquals(initialStock, successCount.get() + remaining,
                "成功扣减数 + 剩余库存应等于初始库存");
        assertTrue(successCount.get() <= initialStock,
                "成功扣减次数不应超过初始库存");
        assertTrue(remaining >= 0,
                "剩余库存不应为负数");
    }

    @Test
    @DisplayName("TC005: 库存回滚 - 取消订单时应返还库存")
    void rollbackStock_WhenOrderCancelled_ShouldRestoreStock() {
        stockService.initStock(TEST_SKU_ID, 10);
        stockService.deductStock(TEST_SKU_ID, 3);
        boolean result = stockService.rollbackStock(TEST_SKU_ID, 3);
        assertTrue(result, "回滚应成功");
        assertEquals(10L, stockService.getStock(TEST_SKU_ID), "库存应恢复为10");
    }

    @Test
    @DisplayName("TC006: 扣减后库存为0 - 再次扣减应失败")
    void deductStock_WhenStockZero_ShouldFail() {
        stockService.initStock(TEST_SKU_ID, 1);
        stockService.deductStock(TEST_SKU_ID, 1);
        boolean result = stockService.deductStock(TEST_SKU_ID, 1);
        assertFalse(result, "库存为0时扣减应失败");
    }

    @Test
    @DisplayName("TC007: 初始化库存 - 应正确设置Redis库存值")
    void initStock_ShouldSetCorrectValue() {
        stockService.initStock(TEST_SKU_ID, 100);
        assertEquals(100L, stockService.getStock(TEST_SKU_ID), "初始化库存应为100");
    }

    @Test
    @DisplayName("TC008: 批量扣减 - 多个SKU同时扣减应各自独立")
    void deductStock_MultipleSkus_ShouldBeIndependent() {
        stockService.initStock(10001L, 10);
        stockService.initStock(10002L, 5);
        boolean result1 = stockService.deductStock(10001L, 3);
        boolean result2 = stockService.deductStock(10002L, 2);
        assertTrue(result1 && result2, "两个SKU扣减都应成功");
        assertEquals(7L, stockService.getStock(10001L), "SKU1剩余应为7");
        assertEquals(3L, stockService.getStock(10002L), "SKU2剩余应为3");
        redisTemplate.delete("stock:sku:10002");
    }

    @Test
    @DisplayName("TC009: 扣减数量为0或负数 - 应直接失败")
    void deductStock_WhenQuantityInvalid_ShouldFail() {
        boolean result1 = stockService.deductStock(TEST_SKU_ID, 0);
        boolean result2 = stockService.deductStock(TEST_SKU_ID, -1);
        assertFalse(result1, "扣减0应失败");
        assertFalse(result2, "扣减负数应失败");
    }

    @Test
    @DisplayName("TC010: 回滚数量为0或负数 - 应直接失败")
    void rollbackStock_WhenQuantityInvalid_ShouldFail() {
        boolean result1 = stockService.rollbackStock(TEST_SKU_ID, 0);
        boolean result2 = stockService.rollbackStock(TEST_SKU_ID, -1);
        assertFalse(result1, "回滚0应失败");
        assertFalse(result2, "回滚负数应失败");
    }

    @Test
    @DisplayName("TC011: 删除库存 - 应正确删除Redis库存")
    void removeStock_ShouldDeleteFromRedis() {
        stockService.initStock(TEST_SKU_ID, 100);
        stockService.removeStock(TEST_SKU_ID);
        assertEquals(0L, stockService.getStock(TEST_SKU_ID), "删除后库存应为0");
    }

    @Test
    @DisplayName("TC012: 同步数据库库存 - 应正确设置Redis库存")
    void syncStockFromDb_ShouldSetCorrectValue() {
        stockService.syncStockFromDb(TEST_SKU_ID, 200);
        assertEquals(200L, stockService.getStock(TEST_SKU_ID), "同步后库存应为200");
    }
}
