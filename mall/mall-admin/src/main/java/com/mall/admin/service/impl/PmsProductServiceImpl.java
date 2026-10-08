package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.ProductParam;
import com.mall.admin.dto.SkuStockParam;
import com.mall.admin.dto.UpdatePublishStatusParam;
import com.mall.admin.service.PmsProductService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.PmsProductCategoryRelationMapper;
import com.mall.mbg.mapper.PmsProductMapper;
import com.mall.mbg.mapper.PmsSkuMapper;
import com.mall.mbg.mapper.PmsSkuStockMapper;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.PmsProductCategoryRelation;
import com.mall.mbg.model.PmsSku;
import com.mall.mbg.model.PmsSkuStock;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PmsProductServiceImpl implements PmsProductService {

    private final PmsProductMapper productMapper;
    private final PmsProductCategoryRelationMapper productCategoryRelationMapper;
    private final PmsSkuMapper skuMapper;
    private final PmsSkuStockMapper skuStockMapper;

    @Override
    @Transactional
    public CommonResult create(ProductParam param) {
        PmsProduct product = new PmsProduct();
        BeanUtils.copyProperties(param, product);
        product.setId(null);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);

        saveCategoryRelations(product.getId(), param.getCategoryIds());
        saveSkuList(product.getId(), param.getSkuList());

        return CommonResult.success(product);
    }

    @Override
    @Transactional
    public CommonResult update(Long id, ProductParam param) {
        PmsProduct existing = productMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("商品不存在");
        }

        PmsProduct product = new PmsProduct();
        BeanUtils.copyProperties(param, product);
        product.setId(id);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        if (param.getCategoryIds() != null) {
            LambdaQueryWrapper<PmsProductCategoryRelation> relWrapper = new LambdaQueryWrapper<>();
            relWrapper.eq(PmsProductCategoryRelation::getProductId, id);
            productCategoryRelationMapper.delete(relWrapper);
            saveCategoryRelations(id, param.getCategoryIds());
        }

        if (param.getSkuList() != null) {
            LambdaQueryWrapper<PmsSku> skuWrapper = new LambdaQueryWrapper<>();
            skuWrapper.eq(PmsSku::getProductId, id);
            List<PmsSku> existingSkus = skuMapper.selectList(skuWrapper);
            List<Long> existingSkuIds = existingSkus.stream()
                    .map(PmsSku::getId)
                    .collect(Collectors.toList());
            skuMapper.delete(skuWrapper);

            if (!existingSkuIds.isEmpty()) {
                LambdaQueryWrapper<PmsSkuStock> stockWrapper = new LambdaQueryWrapper<>();
                stockWrapper.in(PmsSkuStock::getSkuId, existingSkuIds);
                skuStockMapper.delete(stockWrapper);
            }

            saveSkuList(id, param.getSkuList());
        }

        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult delete(Long id) {
        PmsProduct existing = productMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("商品不存在");
        }
        productMapper.deleteById(id);
        return CommonResult.success("删除成功");
    }

    @Override
    public CommonPage<PmsProduct> list(String keyword, Long categoryId, Long brandId, Integer publishStatus, Integer pageNum, Integer pageSize) {
        Page<PmsProduct> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsProduct> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PmsProduct::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(PmsProduct::getCategoryId, categoryId);
        }
        if (brandId != null) {
            wrapper.eq(PmsProduct::getBrandId, brandId);
        }
        if (publishStatus != null) {
            wrapper.eq(PmsProduct::getPublishStatus, publishStatus);
        }
        wrapper.orderByDesc(PmsProduct::getCreateTime);
        Page<PmsProduct> result = productMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }

    @Override
    public PmsProduct getById(Long id) {
        PmsProduct product = productMapper.selectById(id);
        if (product == null) {
            return null;
        }
        return product;
    }

    @Override
    @Transactional
    public CommonResult updatePublishStatus(UpdatePublishStatusParam param) {
        if (param.getIds() == null || param.getIds().isEmpty()) {
            return CommonResult.failed("商品ID列表不能为空");
        }
        for (Long id : param.getIds()) {
            PmsProduct product = new PmsProduct();
            product.setId(id);
            product.setPublishStatus(param.getPublishStatus());
            product.setUpdateTime(LocalDateTime.now());
            productMapper.updateById(product);
        }
        return CommonResult.success("更新发布状态成功");
    }

    @Override
    @Transactional
    public CommonResult batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return CommonResult.failed("商品ID列表不能为空");
        }
        productMapper.deleteBatchIds(ids);
        return CommonResult.success("批量删除成功");
    }

    private void saveCategoryRelations(Long productId, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        for (Long categoryId : categoryIds) {
            PmsProductCategoryRelation relation = PmsProductCategoryRelation.builder()
                    .productId(productId)
                    .categoryId(categoryId)
                    .build();
            productCategoryRelationMapper.insert(relation);
        }
    }

    @Override
    public List<Long> getCategoryIds(Long productId) {
        LambdaQueryWrapper<PmsProductCategoryRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmsProductCategoryRelation::getProductId, productId);
        List<PmsProductCategoryRelation> relations = productCategoryRelationMapper.selectList(wrapper);
        return relations.stream().map(PmsProductCategoryRelation::getCategoryId).collect(Collectors.toList());
    }

    private void saveSkuList(Long productId, List<SkuStockParam> skuList) {
        if (skuList == null || skuList.isEmpty()) {
            return;
        }
        for (SkuStockParam skuParam : skuList) {
            PmsSku sku = PmsSku.builder()
                    .productId(productId)
                    .skuCode(skuParam.getSkuCode())
                    .price(skuParam.getPrice())
                    .spData(skuParam.getSpData())
                    .pic(skuParam.getPic())
                    .createTime(LocalDateTime.now())
                    .build();
            skuMapper.insert(sku);

            if (skuParam.getStock() != null) {
                PmsSkuStock skuStock = PmsSkuStock.builder()
                        .skuId(sku.getId())
                        .stock(skuParam.getStock())
                        .lockStock(0)
                        .lowStock(0)
                        .build();
                skuStockMapper.insert(skuStock);
            }
        }
    }
}
