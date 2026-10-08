package com.mall.portal.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.mbg.mapper.UmsMemberMapper;
import com.mall.mbg.model.UmsMember;
import com.mall.security.component.MemberUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("memberUserDetailsService")
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {

    private final UmsMemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        LambdaQueryWrapper<UmsMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMember::getPhone, phone);
        UmsMember member = memberMapper.selectOne(wrapper);
        if (member == null) {
            throw new UsernameNotFoundException("会员不存在: " + phone);
        }
        return new MemberUserDetails(member);
    }
}
