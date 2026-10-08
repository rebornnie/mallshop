package com.mall.mbg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.mbg.model.UmsMemberMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UmsMemberMessageMapper extends BaseMapper<UmsMemberMessage> {

    @Update("UPDATE ums_member_message SET is_read = 1 WHERE member_id = #{memberId} AND is_read = 0")
    int markAllReadByMemberId(@Param("memberId") Long memberId);
}
