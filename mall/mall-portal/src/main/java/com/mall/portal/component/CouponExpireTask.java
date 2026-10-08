package com.mall.portal.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mall.mbg.mapper.SmsCouponMapper;
import com.mall.mbg.mapper.UmsMemberCouponMapper;
import com.mall.mbg.model.UmsMemberCoupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponExpireTask {

    private final UmsMemberCouponMapper memberCouponMapper;
    private final SmsCouponMapper couponMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    public void expireCoupons() {
        LambdaUpdateWrapper<UmsMemberCoupon> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UmsMemberCoupon::getStatus, 0)
                .lt(UmsMemberCoupon::getExpireTime, LocalDateTime.now())
                .set(UmsMemberCoupon::getStatus, 2);
        int count = memberCouponMapper.update(null, updateWrapper);
        log.info("优惠券过期处理完成, 共处理{}张", count);
    }
}
