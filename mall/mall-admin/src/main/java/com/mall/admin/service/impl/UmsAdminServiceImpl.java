package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.AdminRegisterParam;
import com.mall.admin.service.UmsAdminService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.common.constant.CommonConstant;
import com.mall.common.util.RedisUtil;
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
import com.mall.security.component.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UmsAdminServiceImpl implements UmsAdminService {

    private final SysAdminMapper adminMapper;
    private final SysAdminRoleMapper adminRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisUtil redisUtil;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    public CommonResult register(AdminRegisterParam param) {
        SysAdmin existing = getAdminByUsername(param.getUsername());
        if (existing != null) {
            return CommonResult.failed("用户名已存在");
        }

        SysAdmin admin = SysAdmin.builder()
                .username(param.getUsername())
                .password(passwordEncoder.encode(param.getPassword()))
                .nickname(param.getNickname())
                .email(param.getEmail())
                .status(1)
                .createTime(LocalDateTime.now())
                .build();
        adminMapper.insert(admin);

        if (param.getRoleIds() != null && !param.getRoleIds().isEmpty()) {
            allocRole(admin.getId(), param.getRoleIds());
        }

        return CommonResult.success(admin);
    }

    @Override
    public CommonResult<Map<String, String>> login(String username, String password) {
        SysAdmin admin = getAdminByUsername(username);
        if (admin == null) {
            return CommonResult.failed("用户名或密码错误");
        }
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return CommonResult.failed("用户名或密码错误");
        }
        if (admin.getStatus() != 1) {
            return CommonResult.failed("账号已被禁用");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", admin.getUsername());
        claims.put("id", admin.getId());
        String token = jwtTokenUtil.generateToken(username, claims, "admin");

        admin.setLoginTime(LocalDateTime.now());
        adminMapper.updateById(admin);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @Override
    public SysAdmin getAdminByUsername(String username) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmin::getUsername, username);
        return adminMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> getAdminInfo(String username) {
        SysAdmin admin = getAdminByUsername(username);
        if (admin == null) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", admin.getId());
        info.put("username", admin.getUsername());
        info.put("nickname", admin.getNickname());
        info.put("avatar", admin.getAvatar());
        info.put("email", admin.getEmail());

        LambdaQueryWrapper<SysAdminRole> adminRoleWrapper = new LambdaQueryWrapper<>();
        adminRoleWrapper.eq(SysAdminRole::getAdminId, admin.getId());
        List<SysAdminRole> adminRoles = adminRoleMapper.selectList(adminRoleWrapper);

        List<String> roleNames = new ArrayList<>();
        List<Long> roleIds = new ArrayList<>();
        for (SysAdminRole adminRole : adminRoles) {
            roleIds.add(adminRole.getRoleId());
            SysRole role = roleMapper.selectById(adminRole.getRoleId());
            if (role != null) {
                roleNames.add(role.getName());
            }
        }
        admin.setRoles(roleNames);
        info.put("roles", roleNames);

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
                List<SysMenu> menuTree = buildMenuTree(menus, 0L);
                info.put("menuList", menuTree);
            } else {
                info.put("menuList", new ArrayList<>());
            }
        } else {
            info.put("menuList", new ArrayList<>());
        }

        return info;
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> allMenus, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (parentId.equals(menu.getParentId())) {
                menu.setChildren(buildMenuTree(allMenus, menu.getId()));
                tree.add(menu);
            }
        }
        return tree;
    }

    @Override
    public CommonPage<SysAdmin> list(String keyword, Integer pageNum, Integer pageSize) {
        Page<SysAdmin> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysAdmin::getUsername, keyword)
                    .or()
                    .like(SysAdmin::getNickname, keyword);
        }
        wrapper.orderByDesc(SysAdmin::getCreateTime);
        Page<SysAdmin> result = adminMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }

    @Override
    public CommonResult update(Long id, SysAdmin admin) {
        SysAdmin existing = adminMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("管理员不存在");
        }
        SysAdmin updateAdmin = new SysAdmin();
        updateAdmin.setId(id);
        if (StringUtils.hasText(admin.getNickname())) {
            updateAdmin.setNickname(admin.getNickname());
        }
        if (StringUtils.hasText(admin.getEmail())) {
            updateAdmin.setEmail(admin.getEmail());
        }
        if (StringUtils.hasText(admin.getAvatar())) {
            updateAdmin.setAvatar(admin.getAvatar());
        }
        if (admin.getStatus() != null) {
            updateAdmin.setStatus(admin.getStatus());
        }
        adminMapper.updateById(updateAdmin);
        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult delete(Long id) {
        SysAdmin existing = adminMapper.selectById(id);
        if (existing == null) {
            return CommonResult.failed("管理员不存在");
        }
        adminMapper.deleteById(id);

        LambdaQueryWrapper<SysAdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdminRole::getAdminId, id);
        adminRoleMapper.delete(wrapper);

        return CommonResult.success("删除成功");
    }

    @Override
    public CommonResult allocRole(Long adminId, List<Long> roleIds) {
        LambdaQueryWrapper<SysAdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdminRole::getAdminId, adminId);
        adminRoleMapper.delete(wrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysAdminRole adminRole = SysAdminRole.builder()
                        .adminId(adminId)
                        .roleId(roleId)
                        .build();
                adminRoleMapper.insert(adminRole);
            }
        }
        return CommonResult.success("分配角色成功");
    }
}
