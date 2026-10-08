package com.mall.admin.controller;

import com.mall.admin.dto.AllocMenuParam;
import com.mall.admin.service.SysRoleService;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.SysMenu;
import com.mall.mbg.model.SysRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sys/role")
@RequiredArgsConstructor
@Tag(name = "系统-角色管理", description = "角色CRUD及菜单分配接口")
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    public CommonResult<List<SysRole>> list() {
        return CommonResult.success(roleService.list());
    }

    @PostMapping("/create")
    @Operation(summary = "创建角色")
    public CommonResult create(@RequestBody SysRole role) {
        return roleService.create(role);
    }

    @PutMapping("/update")
    @Operation(summary = "更新角色")
    public CommonResult update(@RequestBody SysRole role) {
        return roleService.update(role);
    }

    @PutMapping("/allocMenu")
    @Operation(summary = "分配菜单给角色")
    public CommonResult allocMenu(@Valid @RequestBody AllocMenuParam param) {
        return roleService.allocMenu(param.getRoleId(), param.getMenuIds());
    }
}
