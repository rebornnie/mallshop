package com.mall.admin.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.mbg.mapper.SysAdminMapper;
import com.mall.mbg.mapper.SysAdminRoleMapper;
import com.mall.mbg.mapper.SysMenuMapper;
import com.mall.mbg.mapper.SysRoleMapper;
import com.mall.mbg.mapper.SysRoleMenuMapper;
import com.mall.mbg.model.SysAdmin;
import com.mall.mbg.model.SysAdminRole;
import com.mall.mbg.model.SysMenu;
import com.mall.mbg.model.SysRole;
import com.mall.mbg.model.SysRoleMenu;
import com.mall.security.component.AdminUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class AdminSecurityConfig {

    private final SysAdminMapper adminMapper;
    private final SysAdminRoleMapper adminRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("adminUserDetailsService")
    public UserDetailsService adminUserDetailsService() {
        return username -> {
            LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysAdmin::getUsername, username);
            SysAdmin admin = adminMapper.selectOne(wrapper);
            if (admin == null) {
                return null;
            }

            LambdaQueryWrapper<SysAdminRole> adminRoleWrapper = new LambdaQueryWrapper<>();
            adminRoleWrapper.eq(SysAdminRole::getAdminId, admin.getId());
            List<SysAdminRole> adminRoles = adminRoleMapper.selectList(adminRoleWrapper);

            List<Long> roleIds = adminRoles.stream()
                    .map(SysAdminRole::getRoleId)
                    .collect(Collectors.toList());

            List<String> permissions = new ArrayList<>();
            if (!roleIds.isEmpty()) {
                LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
                roleMenuWrapper.in(SysRoleMenu::getRoleId, roleIds);
                List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(roleMenuWrapper);

                List<Long> menuIds = roleMenus.stream()
                        .map(SysRoleMenu::getMenuId)
                        .distinct()
                        .collect(Collectors.toList());

                if (!menuIds.isEmpty()) {
                    List<SysMenu> menus = menuMapper.selectBatchIds(menuIds);
                    for (SysMenu menu : menus) {
                        if (menu.getPermission() != null && !menu.getPermission().isEmpty()) {
                            permissions.add(menu.getPermission());
                        }
                    }
                }

                List<String> roleNames = new ArrayList<>();
                for (Long roleId : roleIds) {
                    SysRole role = roleMapper.selectById(roleId);
                    if (role != null) {
                        roleNames.add(role.getName());
                    }
                }
                admin.setRoles(roleNames);
            }

            return new AdminUserDetails(admin, permissions);
        };
    }
}
