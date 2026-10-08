package com.mall.portal.service;

import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.UmsMemberMessage;

public interface UmsMemberMessageService {

    CommonPage<UmsMemberMessage> list(Long memberId, Integer type, Integer isRead, Integer pageNum, Integer pageSize);

    CommonResult<Long> getUnreadCount(Long memberId);

    CommonResult markRead(Long memberId, Long messageId);

    CommonResult markAllRead(Long memberId);

    void sendMessage(Long memberId, String title, String content, Integer type);
}
