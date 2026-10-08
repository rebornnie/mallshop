package com.mall.portal.controller;

import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.search.domain.EsProduct;
import com.mall.search.service.EsProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pms")
@RequiredArgsConstructor
@ConditionalOnBean(EsProductService.class)
public class EsProductController {

    private final EsProductService esProductService;

    @GetMapping("/search")
    public CommonResult<CommonPage<EsProduct>> search(@RequestParam String keyword,
                                                      @RequestParam(required = false) Long categoryId,
                                                      @RequestParam(required = false) Long brandId,
                                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "5") Integer pageSize,
                                                      @RequestParam(required = false) String sort) {
        CommonPage<EsProduct> result = esProductService.search(keyword, categoryId, brandId, pageNum, pageSize, sort);
        return CommonResult.success(result);
    }

    @PostMapping("/es/importAll")
    public CommonResult<Integer> importAll() {
        int count = esProductService.importAll();
        return CommonResult.success(count);
    }
}
