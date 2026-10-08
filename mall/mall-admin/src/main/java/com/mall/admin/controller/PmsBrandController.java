package com.mall.admin.controller;

import com.mall.admin.dto.BrandParam;
import com.mall.admin.service.PmsBrandService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsBrand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/brand")
@RequiredArgsConstructor
@Tag(name = "品牌管理", description = "品牌CRUD接口")
public class PmsBrandController {

    private final PmsBrandService brandService;

    @GetMapping("/list")
    @Operation(summary = "获取品牌列表")
    public CommonResult<CommonPage<PmsBrand>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(brandService.list(keyword, pageNum, pageSize));
    }

    @PostMapping("/create")
    @Operation(summary = "创建品牌")
    public CommonResult create(@Valid @RequestBody BrandParam param) {
        return brandService.create(param);
    }

    @PutMapping("/update")
    @Operation(summary = "更新品牌")
    public CommonResult update(@RequestParam Long id, @Valid @RequestBody BrandParam param) {
        return brandService.update(id, param);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除品牌")
    public CommonResult delete(@PathVariable Long id) {
        return brandService.delete(id);
    }
}
