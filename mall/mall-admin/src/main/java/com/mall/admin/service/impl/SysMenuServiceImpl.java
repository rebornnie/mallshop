package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.service.SysMenuService;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.SysAdminRoleMapper;
import com.mall.mbg.mapper.SysMenuMapper;
import com.mall.mbg.mapper.SysRoleMenuMapper;
import com.mall.mbg.model.SysAdminRole;
import com.mall.mbg.model.SysMenu;
import com.mall.mbg.model.SysRoleMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysAdminRoleMapper adminRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<SysMenu> list() {
        List<SysMenu> allMenus = menuMapper.selectList(null);
        return buildTree(allMenus);
    }

    @Override
    public CommonResult create(SysMenu menu) {
        menuMapper.insert(menu);
        return CommonResult.success(menu);
    }

    @Override
    public CommonResult update(SysMenu menu) {
        SysMenu existing = menuMapper.selectById(menu.getId());
        if (existing == null) {
            return CommonResult.failed("菜单不存在");
        }
        menuMapper.updateById(menu);
        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult delete(Long id) {
        SysMenu existing = menuMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("菜单不存在");
        }

        LambdaQueryWrapper<SysMenu> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysMenu::getParentId, id);
        Long childCount = menuMapper.selectCount(childWrapper);
        if (childCount > 0) {
            return CommonResult.failed("存在子菜单，无法删除");
        }

        menuMapper.deleteById(id);

        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(SysRoleMenu::getMenuId, id);
        roleMenuMapper.delete(roleMenuWrapper);

        return CommonResult.success("删除成功");
    }

    @Override
    public List<SysMenu> getMenuByAdminId(Long adminId) {
        LambdaQueryWrapper<SysAdminRole> adminRoleWrapper = new LambdaQueryWrapper<>();
        adminRoleWrapper.eq(SysAdminRole::getAdminId, adminId);
        List<SysAdminRole> adminRoles = adminRoleMapper.selectList(adminRoleWrapper);

        List<Long> menuIds = new ArrayList<>();
        for (SysAdminRole adminRole : adminRoles) {
            LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
            roleMenuWrapper.eq(SysRoleMenu::getRoleId, adminRole.getRoleId());
            List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(roleMenuWrapper);
            for (SysRoleMenu roleMenu : roleMenus) {
                menuIds.add(roleMenu.getMenuId());
            }
        }

        menuIds = menuIds.stream().distinct().collect(Collectors.toList());
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<SysMenu> menus = menuMapper.selectBatchIds(menuIds);
        return buildTree(menus);
    }

    private List<SysMenu> buildTree(List<SysMenu> allMenus) {
        List<SysMenu> rootMenus = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                menu.setChildren(findChildren(menu.getId(), allMenus));
                rootMenus.add(menu);
            }
        }
        return rootMenus;
    }

    private List<SysMenu> findChildren(Long parentId, List<SysMenu> allMenus) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (parentId.equals(menu.getParentId())) {
                menu.setChildren(findChildren(menu.getId(), allMenus));
                children.add(menu);
            }
        }
        return children;
    }
}
