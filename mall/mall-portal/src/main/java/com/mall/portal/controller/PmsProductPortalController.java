package com.mall.portal.controller;

import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsBrand;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.PmsProductCategory;
import com.mall.portal.service.PmsProductPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pms")
@RequiredArgsConstructor
@Tag(name = "前台商品")
public class PmsProductPortalController {

    private final PmsProductPortalService productPortalService;

    @GetMapping("/product/list")
    @Operation(summary = "获取商品列表")
    public CommonResult list(@RequestParam(required = false) Long categoryId,
                             @RequestParam(required = false) Long brandId,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false, defaultValue = "createTime") String sort,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        CommonPage<PmsProduct> productPage = productPortalService.list(categoryId, brandId, keyword, sort, pageNum, pageSize);
        return CommonResult.success(productPage);
    }

    @GetMapping("/product/{id}")
    @Operation(summary = "获取商品详情")
    public CommonResult detail(@PathVariable Long id) {
        PmsProduct product = productPortalService.detail(id);
        if (product == null) {
            return CommonResult.failed("商品不存在");
        }
        return CommonResult.success(product);
    }

    @GetMapping("/category/list")
    @Operation(summary = "获取商品分类列表")
    public CommonResult categoryList() {
        List<PmsProductCategory> categoryList = productPortalService.categoryList();
        return CommonResult.success(categoryList);
    }

    @GetMapping("/brand/list")
    @Operation(summary = "获取品牌列表")
    public CommonResult brandList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        CommonPage<PmsBrand> brandPage = productPortalService.brandList(pageNum, pageSize);
        return CommonResult.success(brandPage);
    }
}
