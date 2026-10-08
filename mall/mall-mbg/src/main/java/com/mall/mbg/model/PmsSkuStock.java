package com.mall.mbg.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("pms_sku_stock")
public class PmsSkuStock {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long skuId;

    private Integer stock;

    private Integer lockStock;

    private Integer lowStock;

    /**
     * 乐观锁版本号
     */
    private Integer version;
}
