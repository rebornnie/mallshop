package com.mall.search.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.api.CommonPage;
import com.mall.mbg.mapper.PmsBrandMapper;
import com.mall.mbg.mapper.PmsProductCategoryMapper;
import com.mall.mbg.mapper.PmsProductMapper;
import com.mall.mbg.model.PmsBrand;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.PmsProductCategory;
import com.mall.search.domain.EsProduct;
import com.mall.search.repository.EsProductRepository;
import com.mall.search.service.EsProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(EsProductRepository.class)
public class EsProductServiceImpl implements EsProductService {

    private final PmsProductMapper productMapper;
    private final PmsBrandMapper brandMapper;
    private final PmsProductCategoryMapper productCategoryMapper;
    private final EsProductRepository esProductRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public int importAll() {
        LambdaQueryWrapper<PmsProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PmsProduct::getPublishStatus, 1);
        List<PmsProduct> products = productMapper.selectList(queryWrapper);

        List<EsProduct> esProducts = new ArrayList<>(products.size());
        for (PmsProduct product : products) {
            EsProduct esProduct = convertToEsProduct(product);
            esProducts.add(esProduct);
        }

        Iterable<EsProduct> saved = esProductRepository.saveAll(esProducts);
        int count = 0;
        for (EsProduct ignored : saved) {
            count++;
        }
        return count;
    }

    @Override
    public CommonPage<EsProduct> search(String keyword, Long categoryId, Long brandId, Integer pageNum, Integer pageSize, String sort) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 5;
        }

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(boolBuilder -> {
                    if (StringUtils.hasText(keyword)) {
                        boolBuilder.should(s -> s.match(m -> m.field("name").query(keyword)));
                        boolBuilder.should(s -> s.match(m -> m.field("subtitle").query(keyword)));
                        boolBuilder.minimumShouldMatch("1");
                    }
                    if (categoryId != null) {
                        boolBuilder.filter(f -> f.term(t -> t.field("categoryId").value(categoryId)));
                    }
                    if (brandId != null) {
                        boolBuilder.filter(f -> f.term(t -> t.field("brandId").value(brandId)));
                    }
                    boolBuilder.filter(f -> f.term(t -> t.field("publishStatus").value(1)));
                    return boolBuilder;
                }))
                .withSort(buildSort(sort))
                .withPageable(PageRequest.of(pageNum - 1, pageSize))
                .build();

        SearchHits<EsProduct> searchHits = elasticsearchOperations.search(query, EsProduct.class);

        List<EsProduct> resultList = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        long total = searchHits.getTotalHits();
        return CommonPage.restPage(resultList, total, pageNum, pageSize);
    }

    private org.springframework.data.domain.Sort buildSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Order.desc("sale"));
        }
        return switch (sort) {
            case "price_asc" -> org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Order.asc("price"));
            case "price_desc" -> org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Order.desc("price"));
            default -> org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Order.desc("sale"));
        };
    }

    private EsProduct convertToEsProduct(PmsProduct product) {
        String categoryName = null;
        if (product.getCategoryId() != null) {
            PmsProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                categoryName = category.getName();
            }
        }

        String brandName = null;
        if (product.getBrandId() != null) {
            PmsBrand brand = brandMapper.selectById(product.getBrandId());
            if (brand != null) {
                brandName = brand.getName();
            }
        }

        return EsProduct.builder()
                .id(product.getId())
                .name(product.getName())
                .subtitle(product.getSubtitle())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .brandId(product.getBrandId())
                .brandName(brandName)
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .sale(product.getSale())
                .pic(product.getPic())
                .publishStatus(product.getPublishStatus())
                .newStatus(product.getNewStatus())
                .recommendStatus(product.getRecommendStatus())
                .description(product.getDescription())
                .build();
    }
}
