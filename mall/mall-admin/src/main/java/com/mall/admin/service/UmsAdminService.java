package com.mall.admin.service;

import com.mall.admin.dto.AdminRegisterParam;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.SysAdmin;

import java.util.List;
import java.util.Map;

public interface UmsAdminService {

    CommonResult register(AdminRegisterParam param);

    CommonResult<Map<String, String>> login(String username, String password);

    SysAdmin getAdminByUsername(String username);

    Map<String, Object> getAdminInfo(String username);

    CommonPage<SysAdmin> list(String keyword, Integer pageNum, Integer pageSize);

    CommonResult update(Long id, SysAdmin admin);

    CommonResult delete(Long id);

    CommonResult allocRole(Long adminId, List<Long> roleIds);
}
