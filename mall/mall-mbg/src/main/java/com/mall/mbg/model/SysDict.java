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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_dict")
public class SysDict {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String type;

    private String code;

    private String value;

    private Integer sort;

    private Integer status;

    private String description;

    @TableField("create_time")
    private LocalDateTime createTime;
}
