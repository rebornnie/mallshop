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
@TableName("pms_comment")
public class PmsComment {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long productId;

    private Long memberId;

    private String memberNickName;

    private String memberAvatar;

    private Long orderId;

    private Long orderItemId;

    private Integer star;

    private String content;

    private String pics;

    private String memberIp;

    private Integer showStatus;

    private Integer replyCount;

    private LocalDateTime createTime;

    @TableField(exist = false)
    private List<PmsCommentReplay> replyList;
}
