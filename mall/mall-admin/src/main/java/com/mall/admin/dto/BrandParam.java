package com.mall.admin.dto;

import lombok.Data;

@Data
public class BrandParam {

    private Long id;

    private String name;

    private String firstLetter;

    private String logo;

    private String description;

    private Integer recommendStatus;

    private Integer sort;
}
