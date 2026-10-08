package com.mall.admin.controller;

import com.mall.admin.dto.CategoryParam;
import com.mall.admin.service.PmsCategoryService;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsProductCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
@Tag(name = "商品分类管理", description = "商品分类CRUD接口")
public class PmsCategoryController {

    private final PmsCategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "获取分类树")
    public CommonResult<List<PmsProductCategory>> list() {
        return CommonResult.success(categoryService.list());
    }

    @PostMapping("/create")
    @Operation(summary = "创建分类")
    public CommonResult create(@Valid @RequestBody CategoryParam param) {
        return categoryService.create(param);
    }

    @PutMapping("/update")
    @Operation(summary = "更新分类")
    public CommonResult update(@RequestParam Long id, @Valid @RequestBody CategoryParam param) {
        return categoryService.update(id, param);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public CommonResult delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }
}
