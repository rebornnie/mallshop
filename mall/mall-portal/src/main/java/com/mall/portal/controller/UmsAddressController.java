package com.mall.portal.controller;

import com.mall.common.api.CommonResult;
import com.mall.mbg.model.UmsMemberReceiveAddress;
import com.mall.portal.dto.AddressParam;
import com.mall.portal.service.UmsAddressPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ums/address")
@RequiredArgsConstructor
@Tag(name = "收货地址")
public class UmsAddressController {

    private final UmsAddressPortalService addressPortalService;

    @GetMapping("/list")
    @Operation(summary = "获取收货地址列表")
    public CommonResult list() {
        List<UmsMemberReceiveAddress> addressList = addressPortalService.list();
        return CommonResult.success(addressList);
    }

    @PostMapping("/add")
    @Operation(summary = "添加收货地址")
    public CommonResult add(@Valid @RequestBody AddressParam param) {
        return addressPortalService.add(param);
    }

    @PutMapping("/update")
    @Operation(summary = "更新收货地址")
    public CommonResult update(@Valid @RequestBody AddressParam param) {
        return addressPortalService.update(param);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除收货地址")
    public CommonResult delete(@PathVariable Long id) {
        return addressPortalService.delete(id);
    }

    @PutMapping("/default/{id}")
    @Operation(summary = "设置默认收货地址")
    public CommonResult setDefault(@PathVariable Long id) {
        return addressPortalService.setDefault(id);
    }
}
