package com.mall.admin.service;

import java.util.Map;

public interface StatService {

    Map<String, Object> orderStat(String type);

    Map<String, Object> productStat();

    Map<String, Object> userStat();
}
