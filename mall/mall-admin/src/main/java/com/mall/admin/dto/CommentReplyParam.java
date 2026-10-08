package com.mall.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentReplyParam {

    @NotNull(message = "评论ID不能为空")
    private Long commentId;

    @NotNull(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容最多500字")
    private String content;
}
