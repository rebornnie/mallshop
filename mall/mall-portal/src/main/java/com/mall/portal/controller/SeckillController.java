package com.mall.portal.controller;

import com.mall.common.api.CommonResult;
import com.mall.portal.service.SeckillService;
import com.mall.portal.service.UmsMemberPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀控制器
 *
 * 提供前台秒杀相关接口。
 */
@RestController
@RequestMapping("/api/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;
    private final UmsMemberPortalService memberPortalService;

    /**
     * 秒杀下单
     *
     * @param flashPromotionId 秒杀活动ID
     * @param skuId SKU ID
     * @return 秒杀结果
     */
    @PostMapping("/order/create")
    public CommonResult<String> seckill(@RequestParam Long flashPromotionId,
                                         @RequestParam Long skuId) {
        Long memberId = memberPortalService.getCurrentMember().getId();
        boolean success = seckillService.seckill(flashPromotionId, skuId, memberId);
        if (success) {
            return CommonResult.success("秒杀成功，正在为您创建订单...");
        } else {
            return CommonResult.failed("秒杀失败，商品已售罄或您已参与");
        }
    }

    /**
     * 查询秒杀结果
     *
     * @param flashPromotionId 秒杀活动ID
     * @return 秒杀结果
     */
    @GetMapping("/order/result")
    public CommonResult<String> getSeckillResult(@RequestParam Long flashPromotionId) {
        Long memberId = memberPortalService.getCurrentMember().getId();
        boolean participated = seckillService.hasParticipated(flashPromotionId, memberId);
        if (participated) {
            return CommonResult.success("已参与秒杀");
        } else {
            return CommonResult.success("未参与秒杀");
        }
    }

    /**
     * 获取秒杀库存
     *
     * @param flashPromotionId 秒杀活动ID
     * @param skuId SKU ID
     * @return 当前库存
     */
    @GetMapping("/stock")
    public CommonResult<Long> getStock(@RequestParam Long flashPromotionId,
                                        @RequestParam Long skuId) {
        Long stock = seckillService.getSeckillStock(flashPromotionId, skuId);
        return CommonResult.success(stock);
    }
}
