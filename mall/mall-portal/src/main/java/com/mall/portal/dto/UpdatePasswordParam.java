package com.mall.portal.dto;

import lombok.Data;

@Data
public class UpdatePasswordParam {

    private String oldPassword;
    private String newPassword;
}
