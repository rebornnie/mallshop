package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.service.SysRoleService;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.SysRoleMapper;
import com.mall.mbg.mapper.SysRoleMenuMapper;
import com.mall.mbg.model.SysMenu;
import com.mall.mbg.model.SysRole;
import com.mall.mbg.model.SysRoleMenu;
import com.mall.mbg.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    @Override
    public List<SysRole> list() {
        return roleMapper.selectList(null);
    }

    @Override
    public CommonResult create(SysRole role) {
        roleMapper.insert(role);
        return CommonResult.success(role);
    }

    @Override
    public CommonResult update(SysRole role) {
        SysRole existing = roleMapper.selectById(role.getId());
        if (existing == null) {
            return CommonResult.failed("角色不存在");
        }
        roleMapper.updateById(role);
        return CommonResult.success("更新成功");
    }

    @Override
    @Transactional
    public CommonResult allocMenu(Long roleId, List<Long> menuIds) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);

        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = SysRoleMenu.builder()
                        .roleId(roleId)
                        .menuId(menuId)
                        .build();
                roleMenuMapper.insert(roleMenu);
            }
        }
        return CommonResult.success("分配菜单成功");
    }

    @Override
    public List<SysMenu> getMenuList(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);

        List<SysMenu> menus = new ArrayList<>();
        for (SysRoleMenu roleMenu : roleMenus) {
            SysMenu menu = menuMapper.selectById(roleMenu.getMenuId());
            if (menu != null) {
                menus.add(menu);
            }
        }
        return menus;
    }
}
