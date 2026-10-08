package com.mall.portal.controller;

import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.UmsMember;
import com.mall.mbg.model.UmsMemberMessage;
import com.mall.portal.service.UmsMemberMessageService;
import com.mall.portal.service.UmsMemberPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ums/message")
@RequiredArgsConstructor
@Tag(name = "消息通知")
public class UmsMemberMessageController {

    private final UmsMemberMessageService messageService;
    private final UmsMemberPortalService memberPortalService;

    @GetMapping("/list")
    @Operation(summary = "获取消息列表")
    public CommonResult<CommonPage<UmsMemberMessage>> list(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        UmsMember member = memberPortalService.getCurrentMember();
        return CommonResult.success(messageService.list(member.getId(), type, isRead, pageNum, pageSize));
    }

    @GetMapping("/unreadCount")
    @Operation(summary = "获取未读消息数量")
    public CommonResult<Long> getUnreadCount() {
        UmsMember member = memberPortalService.getCurrentMember();
        return messageService.getUnreadCount(member.getId());
    }

    @PutMapping("/markRead")
    @Operation(summary = "标记消息已读")
    public CommonResult markRead(@RequestParam Long messageId) {
        UmsMember member = memberPortalService.getCurrentMember();
        return messageService.markRead(member.getId(), messageId);
    }

    @PutMapping("/markAllRead")
    @Operation(summary = "一键已读")
    public CommonResult markAllRead() {
        UmsMember member = memberPortalService.getCurrentMember();
        return messageService.markAllRead(member.getId());
    }
}
