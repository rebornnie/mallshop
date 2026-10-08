package com.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.api.CommonResult;
import com.mall.common.exception.ApiException;
import com.mall.mbg.mapper.UmsMemberMapper;
import com.mall.mbg.model.UmsMember;
import com.mall.portal.dto.MemberRegisterParam;
import com.mall.portal.dto.UpdateMemberParam;
import com.mall.portal.dto.UpdatePasswordParam;
import com.mall.portal.service.UmsMemberPortalService;
import com.mall.security.component.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UmsMemberPortalServiceImpl implements UmsMemberPortalService {

    private final UmsMemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    public CommonResult register(MemberRegisterParam param) {
        LambdaQueryWrapper<UmsMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMember::getPhone, param.getPhone());
        UmsMember existing = memberMapper.selectOne(wrapper);
        if (existing != null) {
            return CommonResult.failed("手机号已注册");
        }

        UmsMember member = UmsMember.builder()
                .phone(param.getPhone())
                .password(passwordEncoder.encode(param.getPassword()))
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        memberMapper.insert(member);

        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", member.getPhone());
        claims.put("id", member.getId());
        String token = jwtTokenUtil.generateToken(param.getPhone(), claims, "member");

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @Override
    public CommonResult<Map<String, String>> login(String phone, String password) {
        LambdaQueryWrapper<UmsMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMember::getPhone, phone);
        UmsMember member = memberMapper.selectOne(wrapper);
        if (member == null) {
            return CommonResult.failed("手机号或密码错误");
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            return CommonResult.failed("手机号或密码错误");
        }
        if (member.getStatus() != 1) {
            return CommonResult.failed("账号已被禁用");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", member.getPhone());
        claims.put("id", member.getId());
        String token = jwtTokenUtil.generateToken(phone, claims, "member");

        member.setLoginTime(LocalDateTime.now());
        memberMapper.updateById(member);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @Override
    public UmsMember getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ApiException("用户未登录");
        }
        String phone = authentication.getName();
        LambdaQueryWrapper<UmsMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMember::getPhone, phone);
        UmsMember member = memberMapper.selectOne(wrapper);
        if (member == null) {
            throw new ApiException("用户不存在");
        }
        return member;
    }

    @Override
    public CommonResult updateMember(UpdateMemberParam param) {
        UmsMember member = getCurrentMember();
        if (member == null) {
            return CommonResult.failed("用户不存在");
        }

        UmsMember updateMember = new UmsMember();
        updateMember.setId(member.getId());
        if (param.getNickname() != null) {
            updateMember.setNickname(param.getNickname());
        }
        if (param.getAvatar() != null) {
            updateMember.setAvatar(param.getAvatar());
        }
        if (param.getGender() != null) {
            updateMember.setGender(param.getGender());
        }
        if (param.getBirthday() != null) {
            updateMember.setBirthday(LocalDate.parse(param.getBirthday()));
        }
        updateMember.setUpdateTime(LocalDateTime.now());
        memberMapper.updateById(updateMember);
        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult updatePassword(UpdatePasswordParam param) {
        UmsMember member = getCurrentMember();
        if (member == null) {
            return CommonResult.failed("用户不存在");
        }
        if (!passwordEncoder.matches(param.getOldPassword(), member.getPassword())) {
            return CommonResult.failed("旧密码错误");
        }

        UmsMember updateMember = new UmsMember();
        updateMember.setId(member.getId());
        updateMember.setPassword(passwordEncoder.encode(param.getNewPassword()));
        updateMember.setUpdateTime(LocalDateTime.now());
        memberMapper.updateById(updateMember);
        return CommonResult.success("密码修改成功");
    }
}
