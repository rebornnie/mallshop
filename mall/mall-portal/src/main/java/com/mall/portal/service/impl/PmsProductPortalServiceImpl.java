package com.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.api.CommonPage;
import com.mall.mbg.mapper.PmsBrandMapper;
import com.mall.mbg.mapper.PmsProductCategoryMapper;
import com.mall.mbg.mapper.PmsProductMapper;
import com.mall.mbg.mapper.PmsSkuMapper;
import com.mall.mbg.mapper.PmsSkuStockMapper;
import com.mall.mbg.model.PmsBrand;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.PmsProductCategory;
import com.mall.mbg.model.PmsSku;
import com.mall.mbg.model.PmsSkuStock;
import com.mall.portal.service.PmsProductPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PmsProductPortalServiceImpl implements PmsProductPortalService {

    private final PmsProductMapper productMapper;
    private final PmsProductCategoryMapper categoryMapper;
    private final PmsBrandMapper brandMapper;
    private final PmsSkuMapper skuMapper;
    private final PmsSkuStockMapper skuStockMapper;

    @Override
    public CommonPage<PmsProduct> list(Long categoryId, Long brandId, String keyword, String sort, Integer pageNum, Integer pageSize) {
        Page<PmsProduct> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmsProduct::getPublishStatus, 1);
        if (categoryId != null) {
            wrapper.eq(PmsProduct::getCategoryId, categoryId);
        }
        if (brandId != null) {
            wrapper.eq(PmsProduct::getBrandId, brandId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PmsProduct::getName, keyword);
        }
        if ("sale".equals(sort)) {
            wrapper.orderByDesc(PmsProduct::getSale);
        } else if ("price".equals(sort)) {
            wrapper.orderByAsc(PmsProduct::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(PmsProduct::getPrice);
        } else {
            wrapper.orderByDesc(PmsProduct::getCreateTime);
        }
        Page<PmsProduct> result = productMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }

    @Override
    public PmsProduct detail(Long id) {
        PmsProduct product = productMapper.selectById(id);
        if (product == null) {
            return null;
        }

        LambdaQueryWrapper<PmsSku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(PmsSku::getProductId, id);
        List<PmsSku> skuList = skuMapper.selectList(skuWrapper);
        for (PmsSku sku : skuList) {
            LambdaQueryWrapper<PmsSkuStock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(PmsSkuStock::getSkuId, sku.getId());
            PmsSkuStock skuStock = skuStockMapper.selectOne(stockWrapper);
            if (skuStock != null) {
                sku.setStock(skuStock.getStock() - (skuStock.getLockStock() != null ? skuStock.getLockStock() : 0));
            } else {
                sku.setStock(0);
            }
        }

        if (product.getBrandId() != null) {
            PmsBrand brand = brandMapper.selectById(product.getBrandId());
            if (brand != null) {
                product.setBrandName(brand.getName());
            }
        }
        if (product.getCategoryId() != null) {
            PmsProductCategory category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                product.setCategoryName(category.getName());
            }
        }

        product.setSkuList(skuList);

        return product;
    }

    @Override
    public List<PmsProductCategory> categoryList() {
        LambdaQueryWrapper<PmsProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmsProductCategory::getShowStatus, 1)
                .orderByAsc(PmsProductCategory::getSort);
        List<PmsProductCategory> allCategories = categoryMapper.selectList(wrapper);
        for (PmsProductCategory cat : allCategories) {
            cat.setChildren(null);
        }
        return allCategories;
    }

    @Override
    public CommonPage<PmsBrand> brandList(Integer pageNum, Integer pageSize) {
        Page<PmsBrand> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PmsBrand::getSort);
        Page<PmsBrand> result = brandMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }

    private List<PmsProductCategory> buildCategoryTree(List<PmsProductCategory> allCategories, Long parentId) {
        List<PmsProductCategory> tree = new ArrayList<>();
        for (PmsProductCategory category : allCategories) {
            if (parentId.equals(category.getParentId())) {
                category.setChildren(buildCategoryTree(allCategories, category.getId()));
                tree.add(category);
            }
        }
        return tree;
    }
}
