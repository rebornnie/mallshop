package com.mall.portal.dto;

import lombok.Data;

@Data
public class UpdateMemberParam {

    private String nickname;
    private String avatar;
    private Integer gender;
    private String birthday;
}
