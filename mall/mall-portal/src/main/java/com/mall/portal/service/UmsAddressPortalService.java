package com.mall.portal.service;

import com.mall.common.api.CommonResult;
import com.mall.mbg.model.UmsMemberReceiveAddress;
import com.mall.portal.dto.AddressParam;

import java.util.List;

public interface UmsAddressPortalService {

    List<UmsMemberReceiveAddress> list();

    CommonResult add(AddressParam param);

    CommonResult update(AddressParam param);

    CommonResult delete(Long id);

    CommonResult setDefault(Long id);
}
