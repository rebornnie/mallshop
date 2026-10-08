package com.mall.admin.controller;

import com.mall.admin.dto.AdminLoginParam;
import com.mall.admin.dto.AdminRegisterParam;
import com.mall.admin.service.UmsAdminService;
import com.mall.common.annotation.RateLimit;
import com.mall.common.api.CommonResult;
import com.mall.common.constant.CommonConstant;
import com.mall.common.util.RedisUtil;
import com.mall.security.component.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "管理员认证", description = "管理员登录、登出及信息接口")
public class UmsAdminController {

    private final UmsAdminService adminService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    @RateLimit(key = "admin_login", limit = 5, window = 60, message = "登录过于频繁，请1分钟后再试")
    public CommonResult login(@Valid @RequestBody AdminLoginParam param) {
        return adminService.login(param.getUsername(), param.getPassword());
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前管理员信息")
    public CommonResult getAdminInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return CommonResult.failed("未登录");
        }
        String username = authentication.getName();
        Map<String, Object> info = adminService.getAdminInfo(username);
        if (info == null) {
            return CommonResult.failed("用户不存在");
        }
        return CommonResult.success(info);
    }

    @PostMapping("/logout")
    @Operation(summary = "管理员登出")
    public CommonResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            jwtTokenUtil.removeToken(username, "admin");
        }
        return CommonResult.success("登出成功");
    }

    @PostMapping("/register")
    @Operation(summary = "管理员注册")
    public CommonResult register(@Valid @RequestBody AdminRegisterParam param) {
        return adminService.register(param);
    }
}
