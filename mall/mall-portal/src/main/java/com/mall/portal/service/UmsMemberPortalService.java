package com.mall.portal.service;

import com.mall.common.api.CommonResult;
import com.mall.mbg.model.UmsMember;
import com.mall.portal.dto.MemberRegisterParam;
import com.mall.portal.dto.UpdateMemberParam;
import com.mall.portal.dto.UpdatePasswordParam;

import java.util.Map;

public interface UmsMemberPortalService {

    CommonResult register(MemberRegisterParam param);

    CommonResult<Map<String, String>> login(String phone, String password);

    UmsMember getCurrentMember();

    CommonResult updateMember(UpdateMemberParam param);

    CommonResult updatePassword(UpdatePasswordParam param);
}
