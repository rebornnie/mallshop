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
@TableName("pms_brand")
public class PmsBrand {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String firstLetter;

    private String logo;

    private String description;

    private Integer recommendStatus;

    private Integer sort;

    @TableField("create_time")
    private LocalDateTime createTime;
}
