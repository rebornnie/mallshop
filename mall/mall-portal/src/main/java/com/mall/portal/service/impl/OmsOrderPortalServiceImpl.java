package com.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.OmsCartMapper;
import com.mall.mbg.mapper.OmsOrderItemMapper;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.mapper.OmsOrderOperateHistoryMapper;
import com.mall.mbg.mapper.PmsProductMapper;
import com.mall.mbg.mapper.PmsSkuStockMapper;
import com.mall.mbg.mapper.SmsCouponMapper;
import com.mall.mbg.mapper.UmsMemberCouponMapper;
import com.mall.mbg.mapper.UmsMemberReceiveAddressMapper;
import com.mall.mbg.model.OmsCart;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.OmsOrderItem;
import com.mall.mbg.model.OmsOrderOperateHistory;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.PmsSkuStock;
import com.mall.mbg.model.SmsCoupon;
import com.mall.mbg.model.UmsMember;
import com.mall.mbg.model.UmsMemberCoupon;
import com.mall.mbg.model.UmsMemberReceiveAddress;
import com.mall.portal.dto.OrderCreateParam;
import com.mall.portal.service.CouponService;
import com.mall.portal.service.OmsOrderPortalService;
import com.mall.portal.service.StockService;
import com.mall.portal.service.UmsMemberMessageService;
import com.mall.portal.service.UmsMemberPortalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OmsOrderPortalServiceImpl implements OmsOrderPortalService {

    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    private final OmsCartMapper cartMapper;
    private final PmsProductMapper productMapper;
    private final PmsSkuStockMapper skuStockMapper;
    private final UmsMemberReceiveAddressMapper addressMapper;
    private final UmsMemberPortalService memberPortalService;
    private final StockService stockService;
    private final RabbitTemplate rabbitTemplate;
    private final CouponService couponService;
    private final UmsMemberCouponMapper memberCouponMapper;
    private final SmsCouponMapper couponMapper;
    private final UmsMemberMessageService messageService;

    @Override
    public CommonResult generateConfirm(List<Long> cartIds) {
        UmsMember member = memberPortalService.getCurrentMember();

        LambdaQueryWrapper<UmsMemberReceiveAddress> addressWrapper = new LambdaQueryWrapper<>();
        addressWrapper.eq(UmsMemberReceiveAddress::getMemberId, member.getId())
                .orderByDesc(UmsMemberReceiveAddress::getDefaultStatus);
        List<UmsMemberReceiveAddress> addressList = addressMapper.selectList(addressWrapper);

        LambdaQueryWrapper<OmsCart> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.in(OmsCart::getId, cartIds)
                .eq(OmsCart::getMemberId, member.getId());
        List<OmsCart> cartItemList = cartMapper.selectList(cartWrapper);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OmsCart cart : cartItemList) {
            if (cart.getPrice() == null || cart.getQuantity() == null) {
                continue;
            }
            totalAmount = totalAmount.add(cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }
        BigDecimal freightAmount = BigDecimal.ZERO;
        BigDecimal payAmount = totalAmount.add(freightAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("addressList", addressList);
        result.put("cartItemList", cartItemList);
        result.put("totalAmount", totalAmount);
        result.put("freightAmount", freightAmount);
        result.put("payAmount", payAmount);

        List<UmsMemberCoupon> unusedCoupons = couponService.getMyCoupons(member.getId(), 0);
        List<Map<String, Object>> availableCoupons = new ArrayList<>();
        for (UmsMemberCoupon memberCoupon : unusedCoupons) {
            BigDecimal discount = couponService.calculateDiscount(memberCoupon.getCouponId(), totalAmount);
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                SmsCoupon coupon = couponMapper.selectById(memberCoupon.getCouponId());
                Map<String, Object> item = new HashMap<>();
                item.put("memberCoupon", memberCoupon);
                item.put("coupon", coupon);
                item.put("discount", discount);
                availableCoupons.add(item);
            }
        }
        result.put("availableCoupons", availableCoupons);
        return CommonResult.success(result);
    }

    @Override
    @Transactional
    public CommonResult<Map<String, Object>> create(OrderCreateParam param) {
        UmsMember member = memberPortalService.getCurrentMember();

        UmsMemberReceiveAddress address = addressMapper.selectById(param.getAddressId());
        if (address == null || !address.getMemberId().equals(member.getId())) {
            return CommonResult.failed("收货地址不存在");
        }

        LambdaQueryWrapper<OmsCart> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.in(OmsCart::getId, param.getCartIds())
                .eq(OmsCart::getMemberId, member.getId());
        List<OmsCart> cartItemList = cartMapper.selectList(cartWrapper);
        if (cartItemList.isEmpty()) {
            return CommonResult.failed("购物车为空");
        }

        // 提前校验优惠券，避免扣减库存后才失败导致库存泄漏
        UmsMemberCoupon memberCoupon = null;
        if (param.getMemberCouponId() != null) {
            memberCoupon = memberCouponMapper.selectById(param.getMemberCouponId());
            if (memberCoupon == null || !memberCoupon.getMemberId().equals(member.getId()) || memberCoupon.getStatus() != 0) {
                return CommonResult.failed("优惠券不可用");
            }
        }

        // Step 1: 校验商品状态 + Redis预扣库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        List<Long> deductedSkuIds = new ArrayList<>();
        List<Integer> deductedQuantities = new ArrayList<>();

        for (OmsCart cart : cartItemList) {
            PmsProduct product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getPublishStatus() != 1) {
                rollbackDeductedStock(deductedSkuIds, deductedQuantities);
                return CommonResult.failed("商品「" + cart.getProductName() + "」已下架");
            }

            // Redis Lua脚本原子性预扣库存
            boolean deductSuccess = stockService.deductStock(cart.getSkuId(), cart.getQuantity());
            if (!deductSuccess) {
                rollbackDeductedStock(deductedSkuIds, deductedQuantities);
                return CommonResult.failed("商品「" + cart.getProductName() + "」库存不足");
            }

            deductedSkuIds.add(cart.getSkuId());
            deductedQuantities.add(cart.getQuantity());

            BigDecimal itemAmount = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);

            OmsOrderItem orderItem = OmsOrderItem.builder()
                    .productId(cart.getProductId())
                    .productPic(cart.getProductPic())
                    .productName(cart.getProductName())
                    .productPrice(cart.getPrice())
                    .productQuantity(cart.getQuantity())
                    .skuId(cart.getSkuId())
                    .spData(cart.getSpData())
                    .build();
            orderItemList.add(orderItem);
        }

        // Step 2: 数据库乐观锁扣减库存（最终一致性保障）
        List<Long> dbDeductedSkuIds = new ArrayList<>();
        List<Integer> dbDeductedQuantities = new ArrayList<>();
        for (int i = 0; i < cartItemList.size(); i++) {
            OmsCart cart = cartItemList.get(i);
            LambdaQueryWrapper<PmsSkuStock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(PmsSkuStock::getSkuId, cart.getSkuId());
            PmsSkuStock skuStock = skuStockMapper.selectOne(stockWrapper);

            if (skuStock == null) {
                rollbackDbDeductedStock(dbDeductedSkuIds, dbDeductedQuantities);
                rollbackDeductedStock(deductedSkuIds, deductedQuantities);
                throw new RuntimeException("SKU库存记录不存在: " + cart.getSkuId());
            }

            // 乐观锁更新：stock - quantity >= 0 AND version = currentVersion
            int affected = skuStockMapper.deductStockWithOptimisticLock(
                    skuStock.getId(), cart.getQuantity(), skuStock.getVersion());

            if (affected == 0) {
                rollbackDbDeductedStock(dbDeductedSkuIds, dbDeductedQuantities);
                rollbackDeductedStock(deductedSkuIds, deductedQuantities);
                log.warn("数据库乐观锁扣减库存失败, skuId={}, 可能并发冲突", cart.getSkuId());
                return CommonResult.failed("商品「" + cart.getProductName() + "」库存不足，请刷新重试");
            }

            dbDeductedSkuIds.add(cart.getSkuId());
            dbDeductedQuantities.add(cart.getQuantity());
        }

        // Step 3: 创建订单
        BigDecimal freightAmount = BigDecimal.ZERO;
        BigDecimal payAmount = totalAmount.add(freightAmount);
        BigDecimal couponDiscount = BigDecimal.ZERO;
        Long usedCouponId = null;

        if (memberCoupon != null) {
            couponDiscount = couponService.calculateDiscount(memberCoupon.getCouponId(), totalAmount);
            if (couponDiscount.compareTo(BigDecimal.ZERO) > 0) {
                payAmount = payAmount.subtract(couponDiscount);
                usedCouponId = memberCoupon.getCouponId();
            }
        }

        String orderSn = UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        OmsOrder order = OmsOrder.builder()
                .orderSn(orderSn)
                .memberId(member.getId())
                .totalAmount(totalAmount)
                .freightAmount(freightAmount)
                .payAmount(payAmount)
                .payType(param.getPayType())
                .status(0)
                .receiverName(address.getName())
                .receiverPhone(address.getPhone())
                .receiverProvince(address.getProvince())
                .receiverCity(address.getCity())
                .receiverDistrict(address.getDistrict())
                .receiverDetailAddress(address.getDetailAddress())
                .note(buildOrderNote(param.getNote(), couponDiscount, usedCouponId))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleteStatus(0)
                .build();
        orderMapper.insert(order);

        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(orderSn);
            orderItemMapper.insert(orderItem);
        }

        if (param.getMemberCouponId() != null && couponDiscount.compareTo(BigDecimal.ZERO) > 0) {
            couponService.useCoupon(param.getMemberCouponId(), order.getId());
        }

        cartMapper.delete(cartWrapper);

        rabbitTemplate.convertAndSend("order.delay.exchange", "order.delay", orderSn);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orderId", order.getId());
        resultMap.put("orderSn", orderSn);
        return CommonResult.success(resultMap);
    }

    /**
     * 回滚已预扣的库存（用于订单创建失败时的补偿）
     */
    private void rollbackDeductedStock(List<Long> skuIds, List<Integer> quantities) {
        for (int i = 0; i < skuIds.size(); i++) {
            try {
                stockService.rollbackStock(skuIds.get(i), quantities.get(i));
            } catch (Exception e) {
                log.error("回滚库存失败, skuId={}", skuIds.get(i), e);
            }
        }
    }

    /**
     * 补偿回滚已扣减的数据库库存（用于乐观锁扣减中途失败时，事务正常返回不会回滚已提交的扣减）
     */
    private void rollbackDbDeductedStock(List<Long> skuIds, List<Integer> quantities) {
        for (int i = 0; i < skuIds.size(); i++) {
            try {
                LambdaQueryWrapper<PmsSkuStock> stockWrapper = new LambdaQueryWrapper<>();
                stockWrapper.eq(PmsSkuStock::getSkuId, skuIds.get(i));
                PmsSkuStock skuStock = skuStockMapper.selectOne(stockWrapper);
                if (skuStock != null) {
                    int affected = skuStockMapper.rollbackStockWithOptimisticLock(
                            skuStock.getId(), quantities.get(i), skuStock.getVersion());
                    if (affected == 0) {
                        log.warn("补偿回滚数据库库存失败, skuId={}", skuIds.get(i));
                    }
                }
            } catch (Exception e) {
                log.error("补偿回滚数据库库存异常, skuId={}", skuIds.get(i), e);
            }
        }
    }

    private String buildOrderNote(String userNote, BigDecimal couponDiscount, Long couponId) {
        if (couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            return userNote;
        }
        StringBuilder sb = new StringBuilder();
        if (userNote != null && !userNote.isEmpty()) {
            sb.append(userNote).append(" | ");
        }
        sb.append("[优惠券:").append(couponId).append(",优惠金额:").append(couponDiscount).append("]");
        return sb.toString();
    }

    @Override
    public CommonPage<OmsOrder> list(Integer status, Integer pageNum, Integer pageSize) {
        UmsMember member = memberPortalService.getCurrentMember();
        Page<OmsOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getMemberId, member.getId());
        if (status != null) {
            wrapper.eq(OmsOrder::getStatus, status);
        }
        wrapper.eq(OmsOrder::getDeleteStatus, 0)
                .orderByDesc(OmsOrder::getCreateTime);
        Page<OmsOrder> result = orderMapper.selectPage(page, wrapper);

        for (OmsOrder order : result.getRecords()) {
            LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OmsOrderItem::getOrderId, order.getId());
            order.setOrderItemList(orderItemMapper.selectList(itemWrapper));
        }

        return CommonPage.restPage(result);
    }

    @Override
    public OmsOrder detail(Long id) {
        OmsOrder order = orderMapper.selectById(id);
        if (order == null) {
            return null;
        }
        UmsMember member = memberPortalService.getCurrentMember();
        if (!order.getMemberId().equals(member.getId())) {
            return null;
        }

        LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OmsOrderItem::getOrderId, id);
        order.setOrderItemList(orderItemMapper.selectList(itemWrapper));
        return order;
    }

    @Override
    @Transactional
    public CommonResult cancel(Long id, String reason) {
        OmsOrder order = orderMapper.selectById(id);
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }
        UmsMember member = memberPortalService.getCurrentMember();
        if (!order.getMemberId().equals(member.getId())) {
            return CommonResult.failed("无权操作");
        }
        if (order.getStatus() != 0) {
            return CommonResult.failed("订单状态不允许取消");
        }

        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(id);
        updateOrder.setStatus(4);
        updateOrder.setCloseTime(LocalDateTime.now());
        updateOrder.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(updateOrder);

        LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OmsOrderItem::getOrderId, id);
        List<OmsOrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        for (OmsOrderItem orderItem : orderItems) {
            if (orderItem.getSkuId() != null) {
                // Step 1: Redis回滚库存
                stockService.rollbackStock(orderItem.getSkuId(), orderItem.getProductQuantity());

                // Step 2: 数据库回滚库存（乐观锁）
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

        OmsOrderOperateHistory history = OmsOrderOperateHistory.builder()
                .orderId(id)
                .operateMan(member.getNickname() != null ? member.getNickname() : member.getPhone())
                .orderStatus(4)
                .note(reason != null ? reason : "用户取消订单")
                .createTime(LocalDateTime.now())
                .build();
        orderOperateHistoryMapper.insert(history);

        messageService.sendMessage(
                order.getMemberId(),
                "订单已取消",
                "您的订单 #" + order.getOrderSn() + " 已取消。如有疑问请联系客服。",
                1
        );

        LambdaQueryWrapper<UmsMemberCoupon> couponWrapper = new LambdaQueryWrapper<>();
        couponWrapper.eq(UmsMemberCoupon::getOrderId, id)
                .eq(UmsMemberCoupon::getMemberId, member.getId());
        List<UmsMemberCoupon> usedCoupons = memberCouponMapper.selectList(couponWrapper);
        for (UmsMemberCoupon usedCoupon : usedCoupons) {
            if (usedCoupon.getStatus() == 1) {
                couponService.returnCoupon(usedCoupon.getId());
            }
        }

        return CommonResult.success("取消订单成功");
    }

    @Override
    @Transactional
    public CommonResult confirmReceive(Long id) {
        OmsOrder order = orderMapper.selectById(id);
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }
        UmsMember member = memberPortalService.getCurrentMember();
        if (!order.getMemberId().equals(member.getId())) {
            return CommonResult.failed("无权操作");
        }
        if (order.getStatus() != 2) {
            return CommonResult.failed("订单状态不允许确认收货");
        }

        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(id);
        updateOrder.setStatus(3);
        updateOrder.setReceiveTime(LocalDateTime.now());
        updateOrder.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(updateOrder);

        OmsOrderOperateHistory history = OmsOrderOperateHistory.builder()
                .orderId(id)
                .operateMan(member.getNickname() != null ? member.getNickname() : member.getPhone())
                .orderStatus(3)
                .note("用户确认收货")
                .createTime(LocalDateTime.now())
                .build();
        orderOperateHistoryMapper.insert(history);
        return CommonResult.success("确认收货成功");
    }
}
