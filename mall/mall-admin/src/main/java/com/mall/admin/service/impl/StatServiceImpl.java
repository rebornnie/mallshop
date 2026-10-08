package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.service.StatService;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.mapper.PmsProductMapper;
import com.mall.mbg.mapper.UmsMemberMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.UmsMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final OmsOrderMapper orderMapper;
    private final PmsProductMapper productMapper;
    private final UmsMemberMapper memberMapper;

    @Override
    public Map<String, Object> orderStat(String type) {
        Map<String, Object> stat = new HashMap<>();

        LambdaQueryWrapper<OmsOrder> allWrapper = new LambdaQueryWrapper<>();
        Long orderCount = orderMapper.selectCount(allWrapper);
        stat.put("orderCount", orderCount);

        BigDecimal totalAmount = orderMapper.selectTotalPayAmount();
        stat.put("totalAmount", totalAmount);

        LambdaQueryWrapper<OmsOrder> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.eq(OmsOrder::getStatus, 6);
        Long refundCount = orderMapper.selectCount(refundWrapper);
        BigDecimal refundRate = orderCount > 0
                ? new BigDecimal(refundCount).divide(new BigDecimal(orderCount), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100))
                : BigDecimal.ZERO;
        stat.put("refundRate", refundRate);

        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int days = "week".equalsIgnoreCase(type) ? 7 : 30;
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            LambdaQueryWrapper<OmsOrder> dayWrapper = new LambdaQueryWrapper<>();
            dayWrapper.between(OmsOrder::getCreateTime, dayStart, dayEnd);
            Long dayCount = orderMapper.selectCount(dayWrapper);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("count", dayCount);
            trend.add(dayData);
        }
        stat.put("trend", trend);

        return stat;
    }

    @Override
    public Map<String, Object> productStat() {
        Map<String, Object> stat = new HashMap<>();

        LambdaQueryWrapper<PmsProduct> hotWrapper = new LambdaQueryWrapper<>();
        hotWrapper.orderByDesc(PmsProduct::getSale).last("LIMIT 10");
        List<PmsProduct> hotProducts = productMapper.selectList(hotWrapper);
        stat.put("hotProducts", hotProducts);

        return stat;
    }

    @Override
    public Map<String, Object> userStat() {
        Map<String, Object> stat = new HashMap<>();

        LambdaQueryWrapper<UmsMember> totalWrapper = new LambdaQueryWrapper<>();
        Long totalCount = memberMapper.selectCount(totalWrapper);
        stat.put("totalUserCount", totalCount);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<UmsMember> newWrapper = new LambdaQueryWrapper<>();
        newWrapper.ge(UmsMember::getCreateTime, todayStart);
        Long newCount = memberMapper.selectCount(newWrapper);
        stat.put("newUserCount", newCount);

        LambdaQueryWrapper<UmsMember> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.ge(UmsMember::getLoginTime, todayStart);
        Long activeCount = memberMapper.selectCount(activeWrapper);
        stat.put("activeUserCount", activeCount);

        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            LambdaQueryWrapper<UmsMember> dayWrapper = new LambdaQueryWrapper<>();
            dayWrapper.between(UmsMember::getCreateTime, dayStart, dayEnd);
            Long dayCount = memberMapper.selectCount(dayWrapper);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("count", dayCount);
            trend.add(dayData);
        }
        stat.put("trend", trend);

        return stat;
    }
}
