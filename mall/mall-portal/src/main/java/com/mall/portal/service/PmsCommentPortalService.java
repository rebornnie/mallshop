package com.mall.portal.service;

import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.model.PmsComment;
import com.mall.portal.dto.CommentSubmitParam;

import java.util.Map;

public interface PmsCommentPortalService {

    CommonResult submitComment(CommentSubmitParam param, Long memberId);

    CommonPage<PmsComment> listByProduct(Long productId, Integer pageNum, Integer pageSize);

    CommonResult<Map<String, Object>> getCommentStatistics(Long productId);
}
