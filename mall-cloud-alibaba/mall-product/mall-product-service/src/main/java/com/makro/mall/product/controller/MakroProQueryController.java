package com.makro.mall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.pojo.dto.MakroProProductDTO;
import com.makro.mall.product.pojo.dto.request.MakroMailProQueryReq;
import com.makro.mall.product.pojo.dto.request.MakroProQueryReq;
import com.makro.mall.product.service.MakroProService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/3/24
 */
@Api(tags = "Makro Pro商品查询接口")
@RestController
@RequestMapping("/api/v1/pro/query")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class MakroProQueryController {

    @Resource
    private MakroProService makroProService;

    @ApiOperation(value = "查询结果")
    @PostMapping
    public BaseResponse<IPage<MakroProProductDTO>> page(@RequestBody MakroMailProQueryReq req) {
        MakroProQueryReq queryReq = new MakroProQueryReq(req);
        return BaseResponse.success(makroProService.queryMakroProProduct(queryReq));
    }

}
