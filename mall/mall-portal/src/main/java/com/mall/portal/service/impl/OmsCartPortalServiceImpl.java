package com.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.api.CommonResult;
import com.mall.mbg.mapper.OmsCartMapper;
import com.mall.mbg.mapper.PmsProductMapper;
import com.mall.mbg.mapper.PmsSkuMapper;
import com.mall.mbg.mapper.PmsSkuStockMapper;
import com.mall.mbg.model.OmsCart;
import com.mall.mbg.model.PmsProduct;
import com.mall.mbg.model.PmsSku;
import com.mall.mbg.model.PmsSkuStock;
import com.mall.mbg.model.UmsMember;
import com.mall.portal.dto.CartAddParam;
import com.mall.portal.dto.CartUpdateParam;
import com.mall.portal.service.OmsCartPortalService;
import com.mall.portal.service.UmsMemberPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OmsCartPortalServiceImpl implements OmsCartPortalService {

    private final OmsCartMapper cartMapper;
    private final PmsProductMapper productMapper;
    private final PmsSkuMapper skuMapper;
    private final PmsSkuStockMapper skuStockMapper;
    private final UmsMemberPortalService memberPortalService;

    @Override
    public CommonResult add(CartAddParam param) {
        UmsMember member = memberPortalService.getCurrentMember();

        PmsProduct product = productMapper.selectById(param.getProductId());
        if (product == null || product.getPublishStatus() != 1) {
            return CommonResult.failed("商品已下架");
        }

        PmsSku sku = skuMapper.selectById(param.getSkuId());
        if (sku == null) {
            return CommonResult.failed("SKU不存在");
        }

        LambdaQueryWrapper<PmsSkuStock> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(PmsSkuStock::getSkuId, param.getSkuId());
        PmsSkuStock skuStock = skuStockMapper.selectOne(stockWrapper);
        if (skuStock == null || skuStock.getStock() - (skuStock.getLockStock() != null ? skuStock.getLockStock() : 0) <= 0) {
            return CommonResult.failed("库存不足");
        }

        OmsCart cart = OmsCart.builder()
                .memberId(member.getId())
                .productId(param.getProductId())
                .skuId(param.getSkuId())
                .quantity(param.getQuantity())
                .price(sku.getPrice())
                .productName(product.getName())
                .productPic(sku.getPic() != null ? sku.getPic() : product.getPic())
                .spData(sku.getSpData())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        cartMapper.insert(cart);
        return CommonResult.success("添加购物车成功");
    }

    @Override
    public List<OmsCart> list() {
        UmsMember member = memberPortalService.getCurrentMember();
        LambdaQueryWrapper<OmsCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsCart::getMemberId, member.getId())
                .orderByDesc(OmsCart::getCreateTime);
        List<OmsCart> cartList = cartMapper.selectList(wrapper);

        for (OmsCart cart : cartList) {
            PmsProduct product = productMapper.selectById(cart.getProductId());
            if (product != null) {
                cart.setProductStatus(product.getPublishStatus());
            }

            LambdaQueryWrapper<PmsSkuStock> stockWrapper = new LambdaQueryWrapper<>();
            stockWrapper.eq(PmsSkuStock::getSkuId, cart.getSkuId());
            PmsSkuStock skuStock = skuStockMapper.selectOne(stockWrapper);
            if (skuStock != null) {
                cart.setStock(skuStock.getStock() - (skuStock.getLockStock() != null ? skuStock.getLockStock() : 0));
            } else {
                cart.setStock(0);
            }
        }

        return cartList;
    }

    @Override
    public CommonResult update(CartUpdateParam param) {
        OmsCart cart = cartMapper.selectById(param.getId());
        if (cart == null) {
            return CommonResult.failed("购物车项不存在");
        }
        UmsMember member = memberPortalService.getCurrentMember();
        if (!cart.getMemberId().equals(member.getId())) {
            return CommonResult.failed("无权操作");
        }

        OmsCart updateCart = new OmsCart();
        updateCart.setId(param.getId());
        updateCart.setQuantity(param.getQuantity());
        updateCart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateById(updateCart);
        return CommonResult.success("更新成功");
    }

    @Override
    public CommonResult delete(Long id) {
        OmsCart cart = cartMapper.selectById(id);
        if (cart == null) {
            return CommonResult.failed("购物车项不存在");
        }
        UmsMember member = memberPortalService.getCurrentMember();
        if (!cart.getMemberId().equals(member.getId())) {
            return CommonResult.failed("无权操作");
        }
        cartMapper.deleteById(id);
        return CommonResult.success("删除成功");
    }

    @Override
    public CommonResult clear() {
        UmsMember member = memberPortalService.getCurrentMember();
        LambdaQueryWrapper<OmsCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsCart::getMemberId, member.getId());
        cartMapper.delete(wrapper);
        return CommonResult.success("清空购物车成功");
    }
}
