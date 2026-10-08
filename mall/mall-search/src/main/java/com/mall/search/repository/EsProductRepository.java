package com.mall.search.repository;

import com.mall.search.domain.EsProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsProductRepository extends ElasticsearchRepository<EsProduct, Long> {

    Page<EsProduct> findByNameOrSubtitle(String name, String subtitle, Pageable pageable);
}
