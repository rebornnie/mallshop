package com.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.UmsMemberMessageMapper;
import com.mall.mbg.model.UmsMemberMessage;
import com.mall.portal.service.UmsMemberMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UmsMemberMessageServiceImpl implements UmsMemberMessageService {

    private final UmsMemberMessageMapper messageMapper;

    @Override
    public CommonPage<UmsMemberMessage> list(Long memberId, Integer type, Integer isRead, Integer pageNum, Integer pageSize) {
        Page<UmsMemberMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UmsMemberMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberMessage::getMemberId, memberId);
        if (type != null) {
            wrapper.eq(UmsMemberMessage::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(UmsMemberMessage::getIsRead, isRead);
        }
        wrapper.orderByDesc(UmsMemberMessage::getCreateTime);
        Page<UmsMemberMessage> result = messageMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }

    @Override
    public CommonResult<Long> getUnreadCount(Long memberId) {
        LambdaQueryWrapper<UmsMemberMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberMessage::getMemberId, memberId)
                .eq(UmsMemberMessage::getIsRead, 0);
        long count = messageMapper.selectCount(wrapper);
        return CommonResult.success(count);
    }

    @Override
    public CommonResult markRead(Long memberId, Long messageId) {
        UmsMemberMessage message = messageMapper.selectById(messageId);
        if (message == null || !message.getMemberId().equals(memberId)) {
            return CommonResult.failed("消息不存在");
        }
        UmsMemberMessage update = new UmsMemberMessage();
        update.setId(messageId);
        update.setIsRead(1);
        messageMapper.updateById(update);
        return CommonResult.success("标记已读成功");
    }

    @Override
    public CommonResult markAllRead(Long memberId) {
        messageMapper.markAllReadByMemberId(memberId);
        return CommonResult.success("全部标记已读成功");
    }

    @Override
    public void sendMessage(Long memberId, String title, String content, Integer type) {
        UmsMemberMessage message = UmsMemberMessage.builder()
                .memberId(memberId)
                .title(title)
                .content(content)
                .type(type)
                .isRead(0)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(message);
    }
}
