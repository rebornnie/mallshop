package com.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.UmsMemberReceiveAddressMapper;
import com.mall.mbg.model.UmsMember;
import com.mall.mbg.model.UmsMemberReceiveAddress;
import com.mall.portal.dto.AddressParam;
import com.mall.portal.service.UmsAddressPortalService;
import com.mall.portal.service.UmsMemberPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UmsAddressPortalServiceImpl implements UmsAddressPortalService {

    private final UmsMemberReceiveAddressMapper addressMapper;
    private final UmsMemberPortalService memberPortalService;

    @Override
    public List<UmsMemberReceiveAddress> list() {
        UmsMember member = memberPortalService.getCurrentMember();
        LambdaQueryWrapper<UmsMemberReceiveAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMemberReceiveAddress::getMemberId, member.getId())
                .orderByDesc(UmsMemberReceiveAddress::getDefaultStatus);
        return addressMapper.selectList(wrapper);
    }

    @Override
    public CommonResult add(AddressParam param) {
        UmsMember member = memberPortalService.getCurrentMember();
        UmsMemberReceiveAddress address = UmsMemberReceiveAddress.builder()
                .memberId(member.getId())
                .name(param.getName())
                .phone(param.getPhone())
                .province(param.getProvince())
                .city(param.getCity())
                .district(param.getDistrict())
                .detailAddress(param.getDetailAddress())
                .defaultStatus(param.getDefaultStatus() != null ? param.getDefaultStatus() : 0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        addressMapper.insert(address);
        return CommonResult.success("添加成功");
    }

    @Override
    public CommonResult update(AddressParam param) {
        UmsMember member = memberPortalService.getCurrentMember();
        UmsMemberReceiveAddress existing = addressMapper.selectById(param.getId());
        if (existing == null || !existing.getMemberId().equals(member.getId())) {
            return CommonResult.failed("地址不存在");
        }

        UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
        address.setId(param.getId());
        if (param.getName() != null) {
            address.setName(param.getName());
        }
        if (param.getPhone() != null) {
            address.setPhone(param.getPhone());
        }
        if (param.getProvince() != null) {
            address.setProvince(param.getProvince());
        }
        if (param.getCity() != null) {
            address.setCity(param.getCity());
        }
        if (param.getDistrict() != null) {
            address.setDistrict(param.getDistrict());
        }
        if (param.getDetailAddress() != null) {
            address.setDetailAddress(param.getDetailAddress());
        }
        if (param.getDefaultStatus() != null) {
            address.setDefaultStatus(param.getDefaultStatus());
        }
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.updateById(address);
        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult delete(Long id) {
        UmsMember member = memberPortalService.getCurrentMember();
        UmsMemberReceiveAddress existing = addressMapper.selectById(id);
        if (existing == null || !existing.getMemberId().equals(member.getId())) {
            return CommonResult.failed("地址不存在");
        }
        addressMapper.deleteById(id);
        return CommonResult.success("删除成功");
    }

    @Override
    @Transactional
    public CommonResult setDefault(Long id) {
        UmsMember member = memberPortalService.getCurrentMember();
        UmsMemberReceiveAddress existing = addressMapper.selectById(id);
        if (existing == null || !existing.getMemberId().equals(member.getId())) {
            return CommonResult.failed("地址不存在");
        }

        LambdaQueryWrapper<UmsMemberReceiveAddress> resetWrapper = new LambdaQueryWrapper<>();
        resetWrapper.eq(UmsMemberReceiveAddress::getMemberId, member.getId())
                .eq(UmsMemberReceiveAddress::getDefaultStatus, 1);
        UmsMemberReceiveAddress resetAll = new UmsMemberReceiveAddress();
        resetAll.setDefaultStatus(0);
        resetAll.setUpdateTime(LocalDateTime.now());
        addressMapper.update(resetAll, resetWrapper);

        UmsMemberReceiveAddress defaultAddr = new UmsMemberReceiveAddress();
        defaultAddr.setId(id);
        defaultAddr.setDefaultStatus(1);
        defaultAddr.setUpdateTime(LocalDateTime.now());
        addressMapper.updateById(defaultAddr);
        return CommonResult.success("设置默认地址成功");
    }
}
