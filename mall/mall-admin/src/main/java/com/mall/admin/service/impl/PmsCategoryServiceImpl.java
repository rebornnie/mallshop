package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.dto.CategoryParam;
import com.mall.admin.service.PmsCategoryService;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.PmsProductCategoryMapper;
import com.mall.mbg.mapper.PmsProductMapper;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.PmsProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PmsCategoryServiceImpl implements PmsCategoryService {

    private final PmsProductCategoryMapper categoryMapper;
    private final PmsProductMapper productMapper;

    @Override
    public CommonResult create(CategoryParam param) {
        PmsProductCategory category = PmsProductCategory.builder()
                .parentId(param.getParentId() != null ? param.getParentId() : 0L)
                .name(param.getName())
                .icon(param.getIcon())
                .sort(param.getSort() != null ? param.getSort() : 0)
                .showStatus(param.getShowStatus() != null ? param.getShowStatus() : 1)
                .createTime(java.time.LocalDateTime.now())
                .build();
        categoryMapper.insert(category);
        return CommonResult.success(category);
    }

    @Override
    public CommonResult update(Long id, CategoryParam param) {
        PmsProductCategory existing = categoryMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("分类不存在");
        }
        PmsProductCategory category = new PmsProductCategory();
        category.setId(id);
        category.setName(param.getName());
        category.setIcon(param.getIcon());
        category.setSort(param.getSort());
        category.setShowStatus(param.getShowStatus());
        if (param.getParentId() != null) {
            category.setParentId(param.getParentId());
        }
        categoryMapper.updateById(category);
        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult delete(Long id) {
        PmsProductCategory existing = categoryMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("分类不存在");
        }

        LambdaQueryWrapper<PmsProduct> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(PmsProduct::getCategoryId, id);
        Long productCount = productMapper.selectCount(productWrapper);
        if (productCount > 0) {
            return CommonResult.failed("该分类下存在商品，无法删除");
        }

        LambdaQueryWrapper<PmsProductCategory> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(PmsProductCategory::getParentId, id);
        Long childCount = categoryMapper.selectCount(childWrapper);
        if (childCount > 0) {
            return CommonResult.failed("该分类下存在子分类，无法删除");
        }

        categoryMapper.deleteById(id);
        return CommonResult.success("删除成功");
    }

    @Override
    public List<PmsProductCategory> list() {
        List<PmsProductCategory> allCategories = categoryMapper.selectList(null);
        return buildTree(allCategories);
    }

    private List<PmsProductCategory> buildTree(List<PmsProductCategory> allCategories) {
        List<PmsProductCategory> rootCategories = new ArrayList<>();
        for (PmsProductCategory category : allCategories) {
            if (category.getParentId() == null || category.getParentId() == 0L) {
                category.setChildren(findChildren(category.getId(), allCategories));
                rootCategories.add(category);
            }
        }
        rootCategories.sort(Comparator.comparingInt(PmsProductCategory::getSort));
        return rootCategories;
    }

    private List<PmsProductCategory> findChildren(Long parentId, List<PmsProductCategory> allCategories) {
        List<PmsProductCategory> children = new ArrayList<>();
        for (PmsProductCategory category : allCategories) {
            if (parentId.equals(category.getParentId())) {
                category.setChildren(findChildren(category.getId(), allCategories));
                children.add(category);
            }
        }
        children.sort(Comparator.comparingInt(PmsProductCategory::getSort));
        return children;
    }
}
