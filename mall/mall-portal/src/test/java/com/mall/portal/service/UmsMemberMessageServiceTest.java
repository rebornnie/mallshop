package com.mall.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.UmsMemberMessageMapper;
import com.mall.mbg.model.UmsMemberMessage;
import com.mall.portal.service.impl.UmsMemberMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("消息通知服务测试")
class UmsMemberMessageServiceTest {

    @Mock
    private UmsMemberMessageMapper messageMapper;

    private UmsMemberMessageServiceImpl messageService;

    private static final Long MEMBER_ID = 10001L;
    private static final Long MESSAGE_ID = 1L;

    @BeforeEach
    void setUp() {
        messageService = new UmsMemberMessageServiceImpl(messageMapper);
    }

    @Test
    @DisplayName("TC001: 发送消息 - 应成功")
    void sendMessage_ShouldSuccess() {
        when(messageMapper.insert(any(UmsMemberMessage.class))).thenReturn(1);

        messageService.sendMessage(MEMBER_ID, "订单支付成功", "您的订单已支付成功，我们将尽快为您发货", 1);

        verify(messageMapper, times(1)).insert(any(UmsMemberMessage.class));
    }

    @Test
    @DisplayName("TC002: 获取消息列表 - 应返回分页结果")
    void list_ShouldReturnPageResult() {
        UmsMemberMessage message1 = createMessage(1L, "订单支付成功", 1, 0);
        UmsMemberMessage message2 = createMessage(2L, "订单已发货", 1, 0);
        Page<UmsMemberMessage> page = new Page<>();
        page.setRecords(Arrays.asList(message1, message2));
        page.setTotal(2);

        when(messageMapper.selectPage(any(Page.class), any())).thenReturn(page);

        CommonPage<UmsMemberMessage> result = messageService.list(MEMBER_ID, null, null, 1, 20);

        assertEquals(2, result.getTotal(), "应返回2条消息");
        assertEquals(2, result.getRecords().size());
    }

    @Test
    @DisplayName("TC003: 按类型筛选消息 - 应只返回订单类型")
    void list_WithTypeFilter_ShouldReturnFiltered() {
        UmsMemberMessage message = createMessage(1L, "订单支付成功", 1, 0);
        Page<UmsMemberMessage> page = new Page<>();
        page.setRecords(Collections.singletonList(message));
        page.setTotal(1);

        when(messageMapper.selectPage(any(Page.class), any())).thenReturn(page);

        CommonPage<UmsMemberMessage> result = messageService.list(MEMBER_ID, 1, null, 1, 20);

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().get(0).getType());
    }

    @Test
    @DisplayName("TC004: 获取未读消息数量 - 应返回正确数量")
    void getUnreadCount_ShouldReturnCorrectCount() {
        when(messageMapper.selectCount(any())).thenReturn(5L);

        CommonResult<Long> result = messageService.getUnreadCount(MEMBER_ID);

        assertEquals(200, result.getCode());
        assertEquals(5L, result.getData());
    }

    @Test
    @DisplayName("TC005: 标记消息已读 - 应成功")
    void markRead_WhenOwnMessage_ShouldSuccess() {
        UmsMemberMessage message = createMessage(MESSAGE_ID, "订单支付成功", 1, 0);
        message.setMemberId(MEMBER_ID);

        when(messageMapper.selectById(MESSAGE_ID)).thenReturn(message);
        when(messageMapper.updateById(any())).thenReturn(1);

        CommonResult result = messageService.markRead(MEMBER_ID, MESSAGE_ID);

        assertEquals(200, result.getCode());
        verify(messageMapper, times(1)).updateById(any());
    }

    @Test
    @DisplayName("TC006: 标记他人消息已读 - 应失败")
    void markRead_WhenNotOwnMessage_ShouldFail() {
        UmsMemberMessage message = createMessage(MESSAGE_ID, "订单支付成功", 1, 0);
        message.setMemberId(99999L);

        when(messageMapper.selectById(MESSAGE_ID)).thenReturn(message);

        CommonResult result = messageService.markRead(MEMBER_ID, MESSAGE_ID);

        assertNotEquals(200, result.getCode(), "不能标记他人的消息为已读");
        verify(messageMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("TC007: 标记不存在的消息 - 应失败")
    void markRead_WhenMessageNotExist_ShouldFail() {
        when(messageMapper.selectById(MESSAGE_ID)).thenReturn(null);

        CommonResult result = messageService.markRead(MEMBER_ID, MESSAGE_ID);

        assertNotEquals(200, result.getCode(), "消息不存在应失败");
    }

    @Test
    @DisplayName("TC008: 一键已读 - 应成功")
    void markAllRead_ShouldSuccess() {
        when(messageMapper.markAllReadByMemberId(MEMBER_ID)).thenReturn(5);

        CommonResult result = messageService.markAllRead(MEMBER_ID);

        assertEquals(200, result.getCode());
        verify(messageMapper, times(1)).markAllReadByMemberId(MEMBER_ID);
    }

    @Test
    @DisplayName("TC009: 按已读状态筛选 - 应只返回未读消息")
    void list_WithIsReadFilter_ShouldReturnUnreadOnly() {
        UmsMemberMessage message = createMessage(1L, "订单支付成功", 1, 0);
        Page<UmsMemberMessage> page = new Page<>();
        page.setRecords(Collections.singletonList(message));
        page.setTotal(1);

        when(messageMapper.selectPage(any(Page.class), any())).thenReturn(page);

        CommonPage<UmsMemberMessage> result = messageService.list(MEMBER_ID, null, 0, 1, 20);

        assertEquals(1, result.getTotal());
        assertEquals(0, result.getRecords().get(0).getIsRead());
    }

    private UmsMemberMessage createMessage(Long id, String title, Integer type, Integer isRead) {
        return UmsMemberMessage.builder()
                .id(id)
                .memberId(MEMBER_ID)
                .title(title)
                .content("消息内容")
                .type(type)
                .isRead(isRead)
                .createTime(LocalDateTime.now())
                .build();
    }
}
