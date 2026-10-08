package com.mall.portal.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.OmsOrderItemMapper;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.mapper.OmsOrderOperateHistoryMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.OmsOrderItem;
import com.mall.mbg.model.OmsOrderOperateHistory;
import com.mall.mbg.model.UmsMember;
import com.mall.portal.config.AlipayConfig;
import com.mall.portal.service.OmsPayPortalService;
import com.mall.portal.service.UmsMemberMessageService;
import com.mall.portal.service.UmsMemberPortalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OmsPayPortalServiceImpl implements OmsPayPortalService {

    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;
    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    private final UmsMemberPortalService memberPortalService;
    private final UmsMemberMessageService messageService;

    @Override
    public CommonResult<Map<String, String>> alipay(Long orderId) {
        UmsMember member = memberPortalService.getCurrentMember();
        OmsOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getMemberId().equals(member.getId())) {
            return CommonResult.failed("订单不存在");
        }
        if (order.getStatus() != 0) {
            return CommonResult.failed("订单状态不允许支付");
        }

        LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OmsOrderItem::getOrderId, orderId);
        List<OmsOrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        StringBuilder subjectBuilder = new StringBuilder();
        for (OmsOrderItem item : orderItems) {
            if (subjectBuilder.length() > 0) {
                subjectBuilder.append("、");
            }
            subjectBuilder.append(item.getProductName());
        }
        String subject = subjectBuilder.toString();
        if (subject.length() > 128) {
            subject = subject.substring(0, 125) + "...";
        }

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + order.getOrderSn() + "\"," +
                "\"total_amount\":\"" + order.getPayAmount().setScale(2, RoundingMode.HALF_UP) + "\"," +
                "\"subject\":\"" + subject + "\"," +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"" +
                "}");

        try {
            String payUrl = alipayClient.pageExecute(request).getBody();
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("payUrl", payUrl);
            return CommonResult.success(resultMap);
        } catch (AlipayApiException e) {
            log.error("支付宝支付失败: {}", e.getMessage(), e);
            return CommonResult.failed("支付宝支付失败");
        }
    }

    @Override
    @Transactional
    public String callback(Map<String, String> params) {
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSignType()
            );
            if (!signVerified) {
                log.error("支付宝回调验签失败");
                return "failure";
            }

            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                return "success";
            }

            String orderSn = params.get("out_trade_no");
            LambdaQueryWrapper<OmsOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(OmsOrder::getOrderSn, orderSn);
            OmsOrder order = orderMapper.selectOne(orderWrapper);
            if (order == null) {
                log.error("支付宝回调订单不存在: {}", orderSn);
                return "failure";
            }

            String totalAmount = params.get("total_amount");
            if (totalAmount == null || totalAmount.isEmpty()) {
                log.error("支付宝回调金额为空");
                return "failure";
            }
            try {
                BigDecimal callbackAmount = new BigDecimal(totalAmount);
                if (order.getPayAmount().setScale(2, RoundingMode.HALF_UP)
                        .compareTo(callbackAmount.setScale(2, RoundingMode.HALF_UP)) != 0) {
                    log.error("支付宝回调金额不匹配: orderId={}, expected={}, actual={}", order.getId(), order.getPayAmount(), totalAmount);
                    return "failure";
                }
            } catch (NumberFormatException e) {
                log.error("支付宝回调金额格式错误: {}", totalAmount);
                return "failure";
            }

            if (order.getStatus() != 0) {
                return "success";
            }

            OmsOrder updateOrder = new OmsOrder();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(1);
            updateOrder.setPayTime(LocalDateTime.now());
            updateOrder.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(updateOrder);

            OmsOrderOperateHistory history = OmsOrderOperateHistory.builder()
                    .orderId(order.getId())
                    .operateMan("system")
                    .orderStatus(1)
                    .note("支付宝支付成功，交易号：" + params.get("trade_no"))
                    .createTime(LocalDateTime.now())
                    .build();
            orderOperateHistoryMapper.insert(history);

            messageService.sendMessage(
                    order.getMemberId(),
                    "订单支付成功",
                    "您的订单 #" + order.getOrderSn() + " 已支付成功，我们将尽快为您发货。",
                    1
            );

            return "success";
        } catch (AlipayApiException e) {
            log.error("支付宝回调处理异常: {}", e.getMessage(), e);
            return "failure";
        }
    }

    @Override
    public CommonResult<Map<String, Object>> payStatus(Long orderId) {
        UmsMember member = memberPortalService.getCurrentMember();
        OmsOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getMemberId().equals(member.getId())) {
            return CommonResult.failed("订单不存在");
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orderId", order.getId());
        resultMap.put("status", order.getStatus());
        String statusDesc = switch (order.getStatus()) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知状态";
        };
        resultMap.put("statusDesc", statusDesc);
        return CommonResult.success(resultMap);
    }
}
