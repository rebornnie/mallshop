package com.mall.admin.dto;

import lombok.Data;

@Data
public class CategoryParam {

    private Long id;

    private Long parentId;

    private String name;

    private String icon;

    private Integer sort;

    private Integer showStatus;
}
