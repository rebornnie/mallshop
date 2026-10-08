package com.mall.portal.dto;

import lombok.Data;

@Data
public class MemberRegisterParam {

    private String phone;
    private String password;
    private String authCode;
}
