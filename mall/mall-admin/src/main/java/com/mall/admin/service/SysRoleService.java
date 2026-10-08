package com.mall.admin.service;

import com.mall.common.api.CommonResult;
import com.mall.mbg.model.SysMenu;
import com.mall.mbg.model.SysRole;

import java.util.List;

public interface SysRoleService {

    List<SysRole> list();

    CommonResult create(SysRole role);

    CommonResult update(SysRole role);

    CommonResult allocMenu(Long roleId, List<Long> menuIds);

    List<SysMenu> getMenuList(Long roleId);
}
