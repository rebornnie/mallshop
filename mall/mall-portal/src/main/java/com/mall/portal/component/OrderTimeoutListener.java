package com.mall.portal.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.mbg.mapper.OmsOrderItemMapper;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.mapper.OmsOrderOperateHistoryMapper;
import com.mall.mbg.mapper.PmsSkuStockMapper;
import com.mall.mbg.mapper.UmsMemberCouponMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.OmsOrderItem;
import com.mall.mbg.model.OmsOrderOperateHistory;
import com.mall.mbg.model.PmsSkuStock;
import com.mall.mbg.model.UmsMemberCoupon;
import com.mall.portal.service.CouponService;
import com.mall.portal.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RabbitListener(queues = "order.close.queue")
@RequiredArgsConstructor
public class OrderTimeoutListener {

    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    private final PmsSkuStockMapper skuStockMapper;
    private final StockService stockService;
    private final UmsMemberCouponMapper memberCouponMapper;
    private final CouponService couponService;

    @RabbitHandler
    @Transactional
    public void onMessage(String orderSn) {
        log.info("收到订单超时消息: {}", orderSn);

        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getOrderSn, orderSn);
        OmsOrder order = orderMapper.selectOne(wrapper);
        if (order == null) {
            log.warn("订单不存在: {}", orderSn);
            return;
        }
        if (order.getStatus() != 0) {
            return;
        }

        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(4);
        updateOrder.setCloseTime(LocalDateTime.now());
        updateOrder.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(updateOrder);

        LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OmsOrderItem::getOrderId, order.getId());
        List<OmsOrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        for (OmsOrderItem orderItem : orderItems) {
            if (orderItem.getSkuId() != null) {
                // Redis回滚库存（与用户取消订单逻辑保持一致）
                stockService.rollbackStock(orderItem.getSkuId(), orderItem.getProductQuantity());

                // 数据库回滚库存（乐观锁：stock + quantity, lock_stock - quantity）
                LambdaQueryWrapper<PmsSkuStock> stockWrapper = new LambdaQueryWrapper<>();
                stockWrapper.eq(PmsSkuStock::getSkuId, orderItem.getSkuId());
                PmsSkuStock skuStock = skuStockMapper.selectOne(stockWrapper);
                if (skuStock != null) {
                    int affected = skuStockMapper.rollbackStockWithOptimisticLock(
                            skuStock.getId(), orderItem.getProductQuantity(), skuStock.getVersion());
                    if (affected == 0) {
                        log.warn("数据库乐观锁回滚库存失败, skuId={}", orderItem.getSkuId());
                    }
                }
            }
        }

        // 返还已使用的优惠券（与用户取消订单逻辑保持一致）
        LambdaQueryWrapper<UmsMemberCoupon> couponWrapper = new LambdaQueryWrapper<>();
        couponWrapper.eq(UmsMemberCoupon::getOrderId, order.getId())
                .eq(UmsMemberCoupon::getMemberId, order.getMemberId());
        List<UmsMemberCoupon> usedCoupons = memberCouponMapper.selectList(couponWrapper);
        for (UmsMemberCoupon usedCoupon : usedCoupons) {
            if (usedCoupon.getStatus() == 1) {
                couponService.returnCoupon(usedCoupon.getId());
            }
        }

        OmsOrderOperateHistory history = OmsOrderOperateHistory.builder()
                .orderId(order.getId())
                .operateMan("system")
                .orderStatus(4)
                .note("订单超时未支付，系统自动取消")
                .createTime(LocalDateTime.now())
                .build();
        orderOperateHistoryMapper.insert(history);

        log.info("订单超时取消成功: {}", orderSn);
    }
}
