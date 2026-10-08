package com.mall.portal.controller;

import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsComment;
import com.mall.mbg.model.UmsMember;
import com.mall.portal.dto.CommentSubmitParam;
import com.mall.portal.service.UmsMemberPortalService;
import com.mall.portal.service.impl.PmsCommentPortalServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pms/comment")
@RequiredArgsConstructor
@Tag(name = "商品评价")
public class PmsCommentController {

    private final PmsCommentPortalServiceImpl commentPortalService;
    private final UmsMemberPortalService memberPortalService;

    @PostMapping("/submit")
    @Operation(summary = "提交评价")
    public CommonResult submitComment(@Valid @RequestBody CommentSubmitParam param) {
        UmsMember member = memberPortalService.getCurrentMember();
        return commentPortalService.submitComment(param, member.getId());
    }

    @GetMapping("/list")
    @Operation(summary = "获取商品评价列表")
    public CommonResult<CommonPage<PmsComment>> listByProduct(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return CommonResult.success(commentPortalService.listByProduct(productId, pageNum, pageSize));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取商品评价统计")
    public CommonResult<Map<String, Object>> getCommentStatistics(@RequestParam Long productId) {
        return commentPortalService.getCommentStatistics(productId);
    }
}
