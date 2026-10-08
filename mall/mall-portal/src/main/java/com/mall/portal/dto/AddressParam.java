package com.mall.portal.dto;

import lombok.Data;

@Data
public class AddressParam {

    private Long id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer defaultStatus;
}
