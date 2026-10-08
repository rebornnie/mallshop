package com.mall.portal.controller;

import com.mall.common.annotation.RateLimit;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.UmsMember;
import com.mall.portal.dto.MemberLoginParam;
import com.mall.portal.dto.MemberRegisterParam;
import com.mall.portal.dto.UpdateMemberParam;
import com.mall.portal.dto.UpdatePasswordParam;
import com.mall.portal.service.UmsMemberPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("portalUmsMemberController")
@RequestMapping("/api/ums")
@RequiredArgsConstructor
@Tag(name = "前台用户")
public class UmsMemberController {

    private final UmsMemberPortalService memberPortalService;

    @PostMapping("/register")
    @Operation(summary = "会员注册")
    @RateLimit(key = "register", limit = 3, window = 60, message = "注册过于频繁，请1分钟后再试")
    public CommonResult register(@Valid @RequestBody MemberRegisterParam param) {
        return memberPortalService.register(param);
    }

    @PostMapping("/login")
    @Operation(summary = "会员登录")
    @RateLimit(key = "login", limit = 5, window = 60, message = "登录过于频繁，请1分钟后再试")
    public CommonResult login(@Valid @RequestBody MemberLoginParam param) {
        return memberPortalService.login(param.getPhone(), param.getPassword());
    }

    @GetMapping("/currentUser")
    @Operation(summary = "获取当前登录会员信息")
    public CommonResult getCurrentUser() {
        UmsMember member = memberPortalService.getCurrentMember();
        if (member == null) {
            return CommonResult.failed("用户不存在");
        }
        return CommonResult.success(member);
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员信息")
    public CommonResult updateMember(@Valid @RequestBody UpdateMemberParam param) {
        return memberPortalService.updateMember(param);
    }

    @PutMapping("/updatePassword")
    @Operation(summary = "修改密码")
    public CommonResult updatePassword(@Valid @RequestBody UpdatePasswordParam param) {
        return memberPortalService.updatePassword(param);
    }
}
