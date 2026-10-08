package com.mall.portal.service;

import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.OmsOrderItemMapper;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.mapper.PmsCommentMapper;
import com.mall.mbg.mapper.PmsCommentReplayMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.OmsOrderItem;
import com.mall.mbg.model.PmsComment;
import com.mall.portal.dto.CommentSubmitParam;
import com.mall.portal.service.impl.PmsCommentPortalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("商品评价服务测试")
class PmsCommentPortalServiceTest {

    @Mock
    private PmsCommentMapper commentMapper;

    @Mock
    private PmsCommentReplayMapper commentReplayMapper;

    @Mock
    private OmsOrderMapper orderMapper;

    @Mock
    private OmsOrderItemMapper orderItemMapper;

    private PmsCommentPortalServiceImpl commentService;

    private static final Long MEMBER_ID = 10001L;
    private static final Long ORDER_ID = 20001L;
    private static final Long ORDER_ITEM_ID = 30001L;
    private static final Long PRODUCT_ID = 40001L;

    @BeforeEach
    void setUp() {
        commentService = new PmsCommentPortalServiceImpl(
                commentMapper, commentReplayMapper, orderMapper, orderItemMapper
        );
    }

    @Test
    @DisplayName("TC001: 已完成订单评价 - 应成功")
    void submitComment_WhenOrderCompleted_ShouldSuccess() {
        OmsOrder order = createOrder(3);
        OmsOrderItem orderItem = createOrderItem();

        when(orderMapper.selectById(ORDER_ID)).thenReturn(order);
        when(orderItemMapper.selectById(ORDER_ITEM_ID)).thenReturn(orderItem);
        when(commentMapper.selectCount(any())).thenReturn(0L);
        when(commentMapper.insert(any())).thenReturn(1);

        CommentSubmitParam param = createCommentParam(5, "商品很好，物流快！");
        CommonResult result = commentService.submitComment(param, MEMBER_ID);

        assertEquals(200, result.getCode(), "评价应成功");
        verify(commentMapper, times(1)).insert(any(PmsComment.class));
    }

    @Test
    @DisplayName("TC002: 未完成订单评价 - 应失败")
    void submitComment_WhenOrderNotCompleted_ShouldFail() {
        OmsOrder order = createOrder(2);

        when(orderMapper.selectById(ORDER_ID)).thenReturn(order);

        CommentSubmitParam param = createCommentParam(5, "商品很好");
        CommonResult result = commentService.submitComment(param, MEMBER_ID);

        assertNotEquals(200, result.getCode(), "未完成订单不可评价");
        verify(commentMapper, never()).insert(any());
    }

    @Test
    @DisplayName("TC003: 重复评价 - 应失败")
    void submitComment_WhenAlreadyCommented_ShouldFail() {
        OmsOrder order = createOrder(3);
        OmsOrderItem orderItem = createOrderItem();

        when(orderMapper.selectById(ORDER_ID)).thenReturn(order);
        when(orderItemMapper.selectById(ORDER_ITEM_ID)).thenReturn(orderItem);
        when(commentMapper.selectCount(any())).thenReturn(1L);

        CommentSubmitParam param = createCommentParam(5, "商品很好");
        CommonResult result = commentService.submitComment(param, MEMBER_ID);

        assertNotEquals(200, result.getCode(), "重复评价应失败");
        verify(commentMapper, never()).insert(any());
    }

    @Test
    @DisplayName("TC004: 非本人订单评价 - 应失败")
    void submitComment_WhenNotOwnOrder_ShouldFail() {
        OmsOrder order = createOrder(3);
        order.setMemberId(99999L);

        when(orderMapper.selectById(ORDER_ID)).thenReturn(order);

        CommentSubmitParam param = createCommentParam(5, "商品很好");
        CommonResult result = commentService.submitComment(param, MEMBER_ID);

        assertNotEquals(200, result.getCode(), "非本人订单不可评价");
    }

    @Test
    @DisplayName("TC005: 订单商品不匹配 - 应失败")
    void submitComment_WhenOrderItemNotMatch_ShouldFail() {
        OmsOrder order = createOrder(3);
        OmsOrderItem orderItem = createOrderItem();
        orderItem.setOrderId(99999L);

        when(orderMapper.selectById(ORDER_ID)).thenReturn(order);
        when(orderItemMapper.selectById(ORDER_ITEM_ID)).thenReturn(orderItem);

        CommentSubmitParam param = createCommentParam(5, "商品很好");
        CommonResult result = commentService.submitComment(param, MEMBER_ID);

        assertNotEquals(200, result.getCode(), "订单商品不匹配应失败");
    }

    @Test
    @DisplayName("TC006: 评价统计 - 无评价时应返回0")
    void getCommentStatistics_WhenNoComments_ShouldReturnZero() {
        when(commentMapper.selectList(any())).thenReturn(Collections.emptyList());

        CommonResult<Map<String, Object>> result = commentService.getCommentStatistics(PRODUCT_ID);

        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(0, data.get("totalCount"));
        assertEquals(0.0, data.get("avgStar"));
    }

    @Test
    @DisplayName("TC007: 评价统计 - 多星级评价应正确计算")
    void getCommentStatistics_WithMultipleStars_ShouldCalculateCorrectly() {
        when(commentMapper.selectList(any())).thenReturn(java.util.Arrays.asList(
                createComment(5),
                createComment(5),
                createComment(4),
                createComment(3),
                createComment(1)
        ));

        CommonResult<Map<String, Object>> result = commentService.getCommentStatistics(PRODUCT_ID);

        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(5L, data.get("totalCount"));
        assertEquals(3.6, data.get("avgStar"));
        assertEquals(1L, data.get("star1Count"));
        assertEquals(0L, data.get("star2Count"));
        assertEquals(1L, data.get("star3Count"));
        assertEquals(1L, data.get("star4Count"));
        assertEquals(2L, data.get("star5Count"));
    }

    private OmsOrder createOrder(Integer status) {
        return OmsOrder.builder()
                .id(ORDER_ID)
                .memberId(MEMBER_ID)
                .status(status)
                .totalAmount(new BigDecimal("199.00"))
                .createTime(LocalDateTime.now())
                .build();
    }

    private OmsOrderItem createOrderItem() {
        return OmsOrderItem.builder()
                .id(ORDER_ITEM_ID)
                .orderId(ORDER_ID)
                .productId(PRODUCT_ID)
                .productName("测试商品")
                .productQuantity(1)
                .build();
    }

    private CommentSubmitParam createCommentParam(Integer star, String content) {
        CommentSubmitParam param = new CommentSubmitParam();
        param.setOrderId(ORDER_ID);
        param.setOrderItemId(ORDER_ITEM_ID);
        param.setStar(star);
        param.setContent(content);
        return param;
    }

    private PmsComment createComment(Integer star) {
        return PmsComment.builder()
                .id(1L)
                .productId(PRODUCT_ID)
                .memberId(MEMBER_ID)
                .star(star)
                .content("评价内容")
                .showStatus(1)
                .createTime(LocalDateTime.now())
                .build();
    }
}
