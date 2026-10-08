package com.mall.admin.service;

import com.mall.common.api.CommonPage;
import com.mall.mbg.model.UmsMember;

import java.util.Map;

public interface UmsMemberService {

    CommonPage<UmsMember> list(String keyword, Integer pageNum, Integer pageSize);

    Map<String, Object> getById(Long id);
}
