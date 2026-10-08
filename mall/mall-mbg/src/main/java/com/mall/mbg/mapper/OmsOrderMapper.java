package com.mall.mbg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.mbg.model.OmsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface OmsOrderMapper extends BaseMapper<OmsOrder> {

    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM oms_order")
    BigDecimal selectTotalPayAmount();
}
