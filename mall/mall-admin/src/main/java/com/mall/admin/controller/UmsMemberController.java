package com.mall.admin.controller;

import com.mall.admin.service.UmsMemberService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.UmsMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("adminUmsMemberController")
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
@Tag(name = "会员管理", description = "会员查询接口")
public class UmsMemberController {

    private final UmsMemberService memberService;

    @GetMapping("/list")
    @Operation(summary = "获取会员列表")
    public CommonResult<CommonPage<UmsMember>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(memberService.list(keyword, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取会员详情")
    public CommonResult<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> memberInfo = memberService.getById(id);
        if (memberInfo == null) {
            return CommonResult.failed("会员不存在");
        }
        return CommonResult.success(memberInfo);
    }
}
