package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.service.UmsMemberService;
import com.mall.common.api.CommonPage;
import com.mall.mbg.mapper.OmsOrderMapper;
import com.mall.mbg.mapper.UmsMemberMapper;
import com.mall.mbg.model.OmsOrder;
import com.mall.mbg.model.UmsMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UmsMemberServiceImpl implements UmsMemberService {

    private final UmsMemberMapper memberMapper;
    private final OmsOrderMapper orderMapper;

    @Override
    public CommonPage<UmsMember> list(String keyword, Integer pageNum, Integer pageSize) {
        Page<UmsMember> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UmsMember> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(UmsMember::getNickname, keyword)
                    .or()
                    .like(UmsMember::getPhone, keyword);
        }
        wrapper.orderByDesc(UmsMember::getCreateTime);
        Page<UmsMember> result = memberMapper.selectPage(page, wrapper);
        return CommonPage.restPage(result);
    }

    @Override
    public Map<String, Object> getById(Long id) {
        UmsMember member = memberMapper.selectById(id);
        if (member == null) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", member.getId());
        info.put("phone", member.getPhone());
        info.put("nickname", member.getNickname());
        info.put("avatar", member.getAvatar());
        info.put("gender", member.getGender());
        info.put("birthday", member.getBirthday());
        info.put("status", member.getStatus());
        info.put("createTime", member.getCreateTime());

        LambdaQueryWrapper<OmsOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OmsOrder::getMemberId, id);
        Long orderCount = orderMapper.selectCount(orderWrapper);
        info.put("orderCount", orderCount);

        return info;
    }
}
