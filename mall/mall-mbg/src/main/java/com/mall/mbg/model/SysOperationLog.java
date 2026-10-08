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
@TableName("sys_operation_log")
public class SysOperationLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long adminId;

    private String adminName;

    private String operation;

    private String method;

    private String params;

    private String ip;

    @TableField("create_time")
    private LocalDateTime createTime;
}
