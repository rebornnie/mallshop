package com.mall.admin.controller;

import com.mall.admin.dto.ProductParam;
import com.mall.admin.dto.UpdatePublishStatusParam;
import com.mall.admin.service.PmsProductService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsProduct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品CRUD及状态管理接口")
public class PmsProductController {

    private final PmsProductService productService;

    @GetMapping("/list")
    @Operation(summary = "获取商品列表")
    public CommonResult<CommonPage<PmsProduct>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Integer publishStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(productService.list(keyword, categoryId, brandId, publishStatus, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public CommonResult getById(@PathVariable Long id) {
        PmsProduct product = productService.getById(id);
        if (product == null) {
            return CommonResult.failed("商品不存在");
        }
        List<Long> categoryIds = productService.getCategoryIds(id);
        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("categoryIds", categoryIds);
        return CommonResult.success(result);
    }

    @PostMapping("/create")
    @Operation(summary = "创建商品")
    public CommonResult create(@Valid @RequestBody ProductParam param) {
        return productService.create(param);
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品")
    public CommonResult update(@Valid @RequestBody ProductParam param) {
        return productService.update(param.getId(), param);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public CommonResult delete(@PathVariable Long id) {
        return productService.delete(id);
    }

    @PutMapping("/updatePublishStatus")
    @Operation(summary = "更新商品发布状态")
    public CommonResult updatePublishStatus(@Valid @RequestBody UpdatePublishStatusParam param) {
        return productService.updatePublishStatus(param);
    }

    @DeleteMapping("/batchDelete")
    @Operation(summary = "批量删除商品")
    public CommonResult batchDelete(@RequestBody List<Long> ids) {
        return productService.batchDelete(ids);
    }
}
