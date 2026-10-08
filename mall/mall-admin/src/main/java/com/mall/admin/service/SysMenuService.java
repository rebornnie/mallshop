package com.mall.admin.service;

import com.mall.common.api.CommonResult;
import com.mall.mbg.model.SysMenu;

import java.util.List;

public interface SysMenuService {

    List<SysMenu> list();

    CommonResult create(SysMenu menu);

    CommonResult update(SysMenu menu);

    CommonResult delete(Long id);

    List<SysMenu> getMenuByAdminId(Long adminId);
}
