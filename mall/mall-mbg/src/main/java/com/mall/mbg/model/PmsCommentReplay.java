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
@TableName("pms_comment_replay")
public class PmsCommentReplay {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long commentId;

    private String content;

    private Long adminId;

    private String adminName;

    private LocalDateTime createTime;
}
