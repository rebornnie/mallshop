package com.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.OmsOrderItemMapper;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.mapper.PmsCommentMapper;
import com.mall.mbg.mapper.PmsCommentReplayMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.OmsOrderItem;
import com.mall.mbg.model.PmsComment;
import com.mall.mbg.model.PmsCommentReplay;
import com.mall.portal.dto.CommentSubmitParam;
import com.mall.portal.service.PmsCommentPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PmsCommentPortalServiceImpl implements PmsCommentPortalService {

    private final PmsCommentMapper commentMapper;
    private final PmsCommentReplayMapper commentReplayMapper;
    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;

    @Transactional
    public CommonResult submitComment(CommentSubmitParam param, Long memberId) {
        OmsOrder order = orderMapper.selectById(param.getOrderId());
        if (order == null || !order.getMemberId().equals(memberId)) {
            return CommonResult.failed("订单不存在");
        }
        if (order.getStatus() != 3) {
            return CommonResult.failed("订单未完成，无法评价");
        }

        OmsOrderItem orderItem = orderItemMapper.selectById(param.getOrderItemId());
        if (orderItem == null || !orderItem.getOrderId().equals(param.getOrderId())) {
            return CommonResult.failed("订单商品不存在");
        }

        LambdaQueryWrapper<PmsComment> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(PmsComment::getOrderId, param.getOrderId())
                .eq(PmsComment::getOrderItemId, param.getOrderItemId())
                .eq(PmsComment::getMemberId, memberId);
        Long existCount = commentMapper.selectCount(existWrapper);
        if (existCount > 0) {
            return CommonResult.failed("该商品已评价");
        }

        PmsComment comment = PmsComment.builder()
                .productId(orderItem.getProductId())
                .memberId(memberId)
                .orderId(param.getOrderId())
                .orderItemId(param.getOrderItemId())
                .star(param.getStar())
                .content(param.getContent())
                .pics(param.getPics())
                .showStatus(1)
                .replyCount(0)
                .createTime(LocalDateTime.now())
                .build();
        commentMapper.insert(comment);
        return CommonResult.success("评价成功");
    }

    public CommonPage<PmsComment> listByProduct(Long productId, Integer pageNum, Integer pageSize) {
        Page<PmsComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmsComment::getProductId, productId)
                .eq(PmsComment::getShowStatus, 1)
                .orderByDesc(PmsComment::getCreateTime);
        Page<PmsComment> result = commentMapper.selectPage(page, wrapper);

        for (PmsComment comment : result.getRecords()) {
            LambdaQueryWrapper<PmsCommentReplay> replayWrapper = new LambdaQueryWrapper<>();
            replayWrapper.eq(PmsCommentReplay::getCommentId, comment.getId())
                    .orderByDesc(PmsCommentReplay::getCreateTime);
            comment.setReplyList(commentReplayMapper.selectList(replayWrapper));
        }

        return CommonPage.restPage(result);
    }

    public CommonResult<Map<String, Object>> getCommentStatistics(Long productId) {
        LambdaQueryWrapper<PmsComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmsComment::getProductId, productId)
                .eq(PmsComment::getShowStatus, 1);
        List<PmsComment> commentList = commentMapper.selectList(wrapper);

        long totalCount = commentList.size();
        if (totalCount == 0) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("totalCount", 0);
            emptyResult.put("avgStar", 0.0);
            emptyResult.put("star1Count", 0);
            emptyResult.put("star2Count", 0);
            emptyResult.put("star3Count", 0);
            emptyResult.put("star4Count", 0);
            emptyResult.put("star5Count", 0);
            return CommonResult.success(emptyResult);
        }

        long starSum = 0;
        long star1Count = 0;
        long star2Count = 0;
        long star3Count = 0;
        long star4Count = 0;
        long star5Count = 0;

        for (PmsComment comment : commentList) {
            Integer star = comment.getStar();
            if (star == null) {
                continue;
            }
            starSum += star;
            switch (star) {
                case 1 -> star1Count++;
                case 2 -> star2Count++;
                case 3 -> star3Count++;
                case 4 -> star4Count++;
                case 5 -> star5Count++;
                default -> {
                }
            }
        }

        BigDecimal avgStar = BigDecimal.valueOf(starSum)
                .divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP);

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("avgStar", avgStar.doubleValue());
        result.put("star1Count", star1Count);
        result.put("star2Count", star2Count);
        result.put("star3Count", star3Count);
        result.put("star4Count", star4Count);
        result.put("star5Count", star5Count);
        return CommonResult.success(result);
    }
}
