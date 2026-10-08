package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.BrandParam;
import com.mall.admin.service.PmsBrandService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.PmsBrandMapper;
import com.mall.mbg.model.PmsBrand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PmsBrandServiceImpl implements PmsBrandService {

    private final PmsBrandMapper brandMapper;

    @Override
    public CommonResult create(BrandParam param) {
        PmsBrand brand = PmsBrand.builder()
                .name(param.getName())
                .firstLetter(param.getFirstLetter())
                .logo(param.getLogo())
                .description(param.getDescription())
                .recommendStatus(param.getRecommendStatus() != null ? param.getRecommendStatus() : 0)
                .sort(param.getSort() != null ? param.getSort() : 0)
                .createTime(LocalDateTime.now())
                .build();
        brandMapper.insert(brand);
        return CommonResult.success(brand);
    }

    @Override
    public CommonResult update(Long id, BrandParam param) {
        PmsBrand existing = brandMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("品牌不存在");
        }
        PmsBrand brand = new PmsBrand();
        brand.setId(id);
        brand.setName(param.getName());
        brand.setFirstLetter(param.getFirstLetter());
        brand.setLogo(param.getLogo());
        brand.setDescription(param.getDescription());
        brand.setRecommendStatus(param.getRecommendStatus());
        brand.setSort(param.getSort());
        brandMapper.updateById(brand);
        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult delete(Long id) {
        PmsBrand existing = brandMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("品牌不存在");
        }
        brandMapper.deleteById(id);
        return CommonResult.success("删除成功");
    }

    @Override
    public CommonPage<PmsBrand> list(String keyword, Integer pageNum, Integer pageSize) {
        Page<PmsBrand> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsBrand> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PmsBrand::getName, keyword);
        }
        wrapper.orderByAsc(PmsBrand::getSort);
        Page<PmsBrand> result = brandMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }
}
