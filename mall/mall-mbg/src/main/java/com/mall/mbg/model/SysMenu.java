package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long parentId;

    private String name;

    private String url;

    private String component;

    private String icon;

    private Integer sort;

    private Integer type;

    private String permission;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private List<SysMenu> children;
}
