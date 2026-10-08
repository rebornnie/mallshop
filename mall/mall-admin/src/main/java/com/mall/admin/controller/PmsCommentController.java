package com.mall.admin.controller;

import com.mall.admin.dto.CommentReplyParam;
import com.mall.admin.service.PmsCommentAdminService;
import com.mall.admin.service.UmsAdminService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsComment;
import com.mall.mbg.model.SysAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminPmsCommentController")
@RequestMapping("/api/admin/comment")
@RequiredArgsConstructor
@Tag(name = "评价管理")
public class PmsCommentController {

    private final PmsCommentAdminService commentAdminService;
    private final UmsAdminService umsAdminService;

    @GetMapping("/list")
    @Operation(summary = "获取评价列表")
    public CommonResult<CommonPage<PmsComment>> list(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer showStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return CommonResult.success(commentAdminService.list(productId, showStatus, keyword, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取评价详情")
    public CommonResult<PmsComment> getById(@PathVariable Long id) {
        PmsComment comment = commentAdminService.getById(id);
        if (comment == null) {
            return CommonResult.failed("评价不存在");
        }
        return CommonResult.success(comment);
    }

    @PutMapping("/updateShowStatus")
    @Operation(summary = "更新评价显示状态")
    public CommonResult updateShowStatus(@RequestParam Long id, @RequestParam Integer showStatus) {
        return commentAdminService.updateShowStatus(id, showStatus);
    }

    @PostMapping("/reply")
    @Operation(summary = "回复评价")
    public CommonResult reply(@Valid @RequestBody CommentReplyParam param) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysAdmin admin = umsAdminService.getAdminByUsername(username);
        return commentAdminService.reply(param, admin.getId(), username);
    }
}
