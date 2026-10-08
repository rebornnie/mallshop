package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.DeliverParam;
import com.mall.admin.dto.RefundParam;
import com.mall.admin.service.OmsOrderService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.OmsOrderItemMapper;
import com.mall.mbg.mapper.OmsOrderLogisticsMapper;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.mapper.OmsOrderOperateHistoryMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.OmsOrderItem;
import com.mall.mbg.model.OmsOrderLogistics;
import com.mall.mbg.model.OmsOrderOperateHistory;
import com.mall.portal.service.UmsMemberMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OmsOrderServiceImpl implements OmsOrderService {

    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    private final OmsOrderLogisticsMapper logisticsMapper;
    private final UmsMemberMessageService messageService;

    @Override
    public CommonPage<OmsOrder> list(String orderSn, Integer status, String receiverPhone, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        Page<OmsOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderSn)) {
            wrapper.like(OmsOrder::getOrderSn, orderSn);
        }
        if (status != null) {
            wrapper.eq(OmsOrder::getStatus, status);
        }
        if (StringUtils.hasText(receiverPhone)) {
            wrapper.like(OmsOrder::getReceiverPhone, receiverPhone);
        }
        if (StringUtils.hasText(startTime)) {
            try {
                wrapper.ge(OmsOrder::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                return CommonPage.restPage(new Page<>(pageNum, pageSize));
            }
        }
        if (StringUtils.hasText(endTime)) {
            try {
                wrapper.le(OmsOrder::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception e) {
                return CommonPage.restPage(new Page<>(pageNum, pageSize));
            }
        }
        wrapper.orderByDesc(OmsOrder::getCreateTime);
        Page<OmsOrder> result = orderMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }

    @Override
    public OmsOrder getById(Long id) {
        OmsOrder order = orderMapper.selectById(id);
        if (order == null) {
            return null;
        }

        LambdaQueryWrapper<OmsOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OmsOrderItem::getOrderId, id);
        List<OmsOrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        order.setOrderItemList(orderItems);

        return order;
    }

    @Override
    @Transactional
    public CommonResult deliver(DeliverParam param) {
        OmsOrder order = orderMapper.selectById(param.getOrderId());
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }
        if (order.getStatus() != 1) {
            return CommonResult.failed("订单状态不允许发货");
        }

        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(param.getOrderId());
        updateOrder.setStatus(2);
        updateOrder.setDeliveryTime(LocalDateTime.now());
        updateOrder.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(updateOrder);

        OmsOrderOperateHistory history = OmsOrderOperateHistory.builder()
                .orderId(param.getOrderId())
                .operateMan("admin")
                .orderStatus(2)
                .note("已发货，物流公司：" + param.getDeliveryCompany() + "，物流单号：" + param.getDeliverySn())
                .createTime(LocalDateTime.now())
                .build();
        orderOperateHistoryMapper.insert(history);

        OmsOrderLogistics logistics = OmsOrderLogistics.builder()
                .orderId(param.getOrderId())
                .deliveryCompany(param.getDeliveryCompany())
                .deliverySn(param.getDeliverySn())
                .status(0)
                .detail("[{\"time\":\"" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\",\"desc\":\"【" + param.getDeliveryCompany() + "】已揽收，准备发往下一站\"}]")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        logisticsMapper.insert(logistics);

        messageService.sendMessage(
                order.getMemberId(),
                "订单已发货",
                "您的订单 #" + order.getOrderSn() + " 已发货，物流公司：" + param.getDeliveryCompany() + "，物流单号：" + param.getDeliverySn() + "。",
                1
        );

        return CommonResult.success("发货成功");
    }

    @Override
    @Transactional
    public CommonResult refund(RefundParam param) {
        OmsOrder order = orderMapper.selectById(param.getOrderId());
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }

        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(param.getOrderId());
        updateOrder.setUpdateTime(LocalDateTime.now());

        if (param.getApproved()) {
            updateOrder.setStatus(6);
        } else {
            updateOrder.setStatus(1);
        }
        orderMapper.updateById(updateOrder);

        OmsOrderOperateHistory history = OmsOrderOperateHistory.builder()
                .orderId(param.getOrderId())
                .operateMan("admin")
                .orderStatus(param.getApproved() ? 6 : 1)
                .note(param.getApproved() ? "退款审批通过" : "退款审批拒绝：" + param.getNote())
                .createTime(LocalDateTime.now())
                .build();
        orderOperateHistoryMapper.insert(history);

        return CommonResult.success(param.getApproved() ? "退款审批通过" : "退款审批拒绝");
    }
}
