package com.mall.portal.controller;

import com.mall.common.api.CommonResult;
import com.mall.mbg.model.OmsCart;
import com.mall.portal.dto.CartAddParam;
import com.mall.portal.dto.CartUpdateParam;
import com.mall.portal.service.OmsCartPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oms/cart")
@RequiredArgsConstructor
@Tag(name = "购物车")
public class OmsCartController {

    private final OmsCartPortalService cartPortalService;

    @PostMapping("/add")
    @Operation(summary = "添加商品到购物车")
    public CommonResult add(@Valid @RequestBody CartAddParam param) {
        return cartPortalService.add(param);
    }

    @GetMapping("/list")
    @Operation(summary = "获取购物车列表")
    public CommonResult list() {
        List<OmsCart> cartList = cartPortalService.list();
        return CommonResult.success(cartList);
    }

    @PutMapping("/update")
    @Operation(summary = "更新购物车项")
    public CommonResult update(@Valid @RequestBody CartUpdateParam param) {
        return cartPortalService.update(param);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除购物车项")
    public CommonResult delete(@PathVariable Long id) {
        return cartPortalService.delete(id);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车")
    public CommonResult clear() {
        return cartPortalService.clear();
    }
}
