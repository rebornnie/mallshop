package com.mall.mbg.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mall.mbg.mapper")
public class MapperScanConfig {
}
