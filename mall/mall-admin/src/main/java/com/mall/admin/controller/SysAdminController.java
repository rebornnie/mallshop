package com.mall.admin.controller;

import com.mall.admin.dto.AdminRegisterParam;
import com.mall.admin.service.UmsAdminService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.SysAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sys/admin")
@RequiredArgsConstructor
@Tag(name = "系统-管理员管理", description = "管理员CRUD接口")
public class SysAdminController {

    private final UmsAdminService adminService;

    @GetMapping("/list")
    @Operation(summary = "获取管理员列表")
    public CommonResult<CommonPage<SysAdmin>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(adminService.list(keyword, pageNum, pageSize));
    }

    @PostMapping("/create")
    @Operation(summary = "创建管理员")
    public CommonResult create(@RequestBody AdminRegisterParam param) {
        return adminService.register(param);
    }

    @PutMapping("/update")
    @Operation(summary = "更新管理员")
    public CommonResult update(@RequestParam Long id, @RequestBody SysAdmin admin) {
        return adminService.update(id, admin);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理员")
    public CommonResult delete(@PathVariable Long id) {
        return adminService.delete(id);
    }
}
