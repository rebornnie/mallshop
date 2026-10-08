package com.mall.mbg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.mbg.model.PmsSkuStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PmsSkuStockMapper extends BaseMapper<PmsSkuStock> {

    /**
     * 乐观锁扣减库存
     *
     * @param id 库存ID
     * @param quantity 扣减数量
     * @param version 当前版本号
     * @return 影响行数，0表示扣减失败（版本冲突或库存不足）
     */
    @Update("UPDATE pms_sku_stock SET stock = stock - #{quantity}, lock_stock = lock_stock + #{quantity}, version = version + 1 WHERE id = #{id} AND stock >= #{quantity} AND version = #{version}")
    int deductStockWithOptimisticLock(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Integer version);

    /**
     * 乐观锁回滚库存
     *
     * @param id 库存ID
     * @param quantity 回滚数量
     * @param version 当前版本号
     * @return 影响行数，0表示回滚失败（版本冲突）
     */
    @Update("UPDATE pms_sku_stock SET stock = stock + #{quantity}, lock_stock = lock_stock - #{quantity}, version = version + 1 WHERE id = #{id} AND lock_stock >= #{quantity} AND version = #{version}")
    int rollbackStockWithOptimisticLock(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Integer version);
}
