package com.mall.admin.controller;

import com.mall.admin.service.SysMenuService;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.SysMenu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sys/menu")
@RequiredArgsConstructor
@Tag(name = "系统-菜单管理", description = "菜单CRUD接口")
public class SysMenuController {

    private final SysMenuService menuService;

    @GetMapping("/list")
    @Operation(summary = "获取菜单树")
    public CommonResult<List<SysMenu>> list() {
        return CommonResult.success(menuService.list());
    }

    @PostMapping("/create")
    @Operation(summary = "创建菜单")
    public CommonResult create(@RequestBody SysMenu menu) {
        return menuService.create(menu);
    }

    @PutMapping("/update")
    @Operation(summary = "更新菜单")
    public CommonResult update(@RequestBody SysMenu menu) {
        return menuService.update(menu);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    public CommonResult delete(@PathVariable Long id) {
        return menuService.delete(id);
    }
}
