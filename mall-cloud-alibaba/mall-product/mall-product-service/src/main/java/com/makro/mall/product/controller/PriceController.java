package com.makro.mall.product.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.product.pojo.entity.ProdPrice;
import com.makro.mall.product.service.ProdPriceService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/22
 **/
@Api(tags = "商品历史价格")
@RestController
@RequestMapping("/api/v1/product/price")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class PriceController {

    private final ProdPriceService priceService;

    @ApiOperation(value = "商品价格分页")
    @GetMapping
    public BaseResponse<IPage<ProdPrice>> list(@ApiParam("页码") Integer page,
                                               @ApiParam("每页数量") Integer limit,
                                               @ApiParam("商品") String itemCode,
                                               @ApiParam("1：默认，0：其他") Integer isvalid) {
        LambdaQueryWrapper<ProdPrice> queryWrapper = new LambdaQueryWrapper<ProdPrice>()
                .eq(StrUtil.isNotBlank(itemCode), ProdPrice::getItemcode, itemCode)
                .eq(isvalid != null, ProdPrice::getIsvalid, isvalid)
                .orderByDesc(ProdPrice::getGmtCreate);
        IPage<ProdPrice> result = priceService.page(new MakroPage<>(page, limit), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "新增商品价格")
    @PostMapping(value = "/add")
    public BaseResponse add(@RequestBody ProdPrice price) {
        price.setId(IdUtil.simpleUUID());
        Boolean saveResult = priceService.save(price);
        return BaseResponse.judge(saveResult);
    }

    @ApiOperation(value = "商品价格详情")
    @GetMapping("/{id}")
    public BaseResponse details(@PathVariable String id) {
        ProdPrice price = priceService.getById(id);
        return BaseResponse.success(price);
    }

    @ApiOperation(value = "更新商品价格")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public BaseResponse update(@PathVariable String id, @RequestBody ProdPrice price) {
        price.setId(id);
        Boolean result = priceService.updateById(price);
        return BaseResponse.judge(result);
    }

}
