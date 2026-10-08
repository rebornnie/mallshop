package com.mall.admin.service;

import com.mall.admin.dto.CommentReplyParam;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsComment;

public interface PmsCommentAdminService {

    CommonPage<PmsComment> list(Long productId, Integer showStatus, String keyword, Integer pageNum, Integer pageSize);

    PmsComment getById(Long id);

    CommonResult updateShowStatus(Long id, Integer showStatus);

    CommonResult reply(CommentReplyParam param, Long adminId, String adminName);
}
