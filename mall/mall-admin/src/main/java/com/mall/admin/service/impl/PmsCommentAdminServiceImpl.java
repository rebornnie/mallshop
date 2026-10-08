package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.CommentReplyParam;
import com.mall.admin.service.PmsCommentAdminService;
import com.mall.common.api.CommonPage;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.PmsCommentMapper;
import com.mall.mbg.mapper.PmsCommentReplayMapper;
import com.mall.mbg.model.PmsComment;
import com.mall.mbg.model.PmsCommentReplay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PmsCommentAdminServiceImpl implements PmsCommentAdminService {

    private final PmsCommentMapper commentMapper;
    private final PmsCommentReplayMapper commentReplayMapper;

    @Override
    public CommonPage<PmsComment> list(Long productId, Integer showStatus, String keyword, Integer pageNum, Integer pageSize) {
        Page<PmsComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsComment> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(PmsComment::getProductId, productId);
        }
        if (showStatus != null) {
            wrapper.eq(PmsComment::getShowStatus, showStatus);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PmsComment::getContent, keyword);
        }
        wrapper.orderByDesc(PmsComment::getCreateTime);
        Page<PmsComment> result = commentMapper.selectPage(page, wrapper);

        for (PmsComment comment : result.getRecords()) {
            LambdaQueryWrapper<PmsCommentReplay> replayWrapper = new LambdaQueryWrapper<>();
            replayWrapper.eq(PmsCommentReplay::getCommentId, comment.getId())
                    .orderByDesc(PmsCommentReplay::getCreateTime);
            comment.setReplyList(commentReplayMapper.selectList(replayWrapper));
        }

        return CommonPage.restPage(result);
    }

    @Override
    public PmsComment getById(Long id) {
        PmsComment comment = commentMapper.selectById(id);
        if (comment == null) {
            return null;
        }
        LambdaQueryWrapper<PmsCommentReplay> replayWrapper = new LambdaQueryWrapper<>();
        replayWrapper.eq(PmsCommentReplay::getCommentId, comment.getId())
                .orderByDesc(PmsCommentReplay::getCreateTime);
        comment.setReplyList(commentReplayMapper.selectList(replayWrapper));
        return comment;
    }

    @Override
    public CommonResult updateShowStatus(Long id, Integer showStatus) {
        PmsComment comment = commentMapper.selectById(id);
        if (comment == null) {
            return CommonResult.failed("评价不存在");
        }
        PmsComment update = new PmsComment();
        update.setId(id);
        update.setShowStatus(showStatus);
        commentMapper.updateById(update);
        return CommonResult.success("更新成功");
    }

    @Override
    @Transactional
    public CommonResult reply(CommentReplyParam param, Long adminId, String adminName) {
        PmsComment comment = commentMapper.selectById(param.getCommentId());
        if (comment == null) {
            return CommonResult.failed("评价不存在");
        }

        PmsCommentReplay replay = PmsCommentReplay.builder()
                .commentId(param.getCommentId())
                .content(param.getContent())
                .adminId(adminId)
                .adminName(adminName)
                .createTime(LocalDateTime.now())
                .build();
        commentReplayMapper.insert(replay);

        PmsComment update = new PmsComment();
        update.setId(comment.getId());
        update.setReplyCount(comment.getReplyCount() + 1);
        commentMapper.updateById(update);

        return CommonResult.success("回复成功");
    }
}
