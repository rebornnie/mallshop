package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("ums_member_message")
public class UmsMemberMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String title;

    private String content;

    private Integer type;

    private Integer isRead;

    private LocalDateTime createTime;
}
