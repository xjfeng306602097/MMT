package com.makro.mall.product.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.product.pojo.entity.ProdData;
import com.makro.mall.product.pojo.entity.ProdTemplateDetails;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import com.makro.mall.product.service.ProdDataService;
import com.makro.mall.product.service.ProdTemplateDetailsService;
import com.makro.mall.product.service.ProdTemplateInfoService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Description: 已作废
 * @Author: zhuangzikai
 * @Date: 2021/12/7
 **/
@Api(tags = "导入Excel信息查询接口")
@RestController
@RequestMapping("/api/v1/product/backup/excel")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class TextThaiController {
    private final ProdTemplateInfoService infoService;
    private final ProdDataService dataService;
    private final ProdTemplateDetailsService detailsService;

    @ApiOperation(value = "商品导入Excel列表分页")
    @GetMapping
    public BaseResponse<IPage<ProdTemplateInfo>> list(@ApiParam("页码") Integer page,
                                                      @ApiParam("每页数量") Integer limit,
                                                      @ApiParam("MM活动编码") String mmCode,
                                                      @ApiParam("用户名") String userName,
                                                      @ApiParam("起始时间") Date begin,
                                                      @ApiParam("终止时间") Date end,
                                                      @ApiParam("1：有效；0：作废删除") Integer isvalid) {
        LambdaQueryWrapper<ProdTemplateInfo> qw = new LambdaQueryWrapper<ProdTemplateInfo>()
                .eq(StrUtil.isNotBlank(mmCode), ProdTemplateInfo::getMmCode, mmCode)
                .like(StrUtil.isNotBlank(userName), ProdTemplateInfo::getCreator, userName)
                .ge(begin != null, ProdTemplateInfo::getGmtCreate, begin)
                .le(end != null, ProdTemplateInfo::getGmtCreate, end)
                .eq(isvalid != null, ProdTemplateInfo::getIsvalid, isvalid)
                .orderByDesc(ProdTemplateInfo::getGmtCreate);
        IPage<ProdTemplateInfo> result = infoService.page(new MakroPage<>(page, limit), qw);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "Excel原生TextThai商品列表分页")
    @GetMapping("/details/{excelId}")
    public BaseResponse<IPage<ProdTemplateDetails>> list(@ApiParam("导入文件主表ID") @PathVariable String excelId,
                                                         @ApiParam("页码") Integer page,
                                                         @ApiParam("每页数量") Integer limit,
                                                         @ApiParam("商品编码") String itemCode,
                                                         @ApiParam("1：有效；0：作废删除") Integer isvalid) {
        LambdaQueryWrapper<ProdTemplateDetails> qw = new LambdaQueryWrapper<ProdTemplateDetails>()
                .eq(StrUtil.isNotBlank(excelId), ProdTemplateDetails::getInfoid, excelId)
                .eq(StrUtil.isNotBlank(itemCode), ProdTemplateDetails::getItemcode, itemCode)
                .eq(isvalid != null, ProdTemplateDetails::getIsvalid, isvalid);
        IPage<ProdTemplateDetails> result = detailsService.page(new MakroPage<>(page, limit), qw);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "Excel设计相关商品信息列表分页")
    @GetMapping("/design/{excelId}")
    public BaseResponse<IPage<ProdData>> designList(@ApiParam("导入文件主表ID") @PathVariable String excelId,
                                                    @ApiParam("页码") Integer page,
                                                    @ApiParam("每页数量") Integer limit,
                                                    @ApiParam("商品编码") String itemCode,
                                                    @ApiParam("1：有效；0：作废删除") Integer isvalid) {
        LambdaQueryWrapper<ProdData> qw = new LambdaQueryWrapper<ProdData>()
                .eq(StrUtil.isNotBlank(excelId), ProdData::getInfoid, excelId)
                .eq(StrUtil.isNotBlank(itemCode), ProdData::getItemcode, itemCode)
                .eq(isvalid != null, ProdData::getIsvalid, isvalid);
        IPage<ProdData> result = dataService.page(new MakroPage<>(page, limit), qw);
        return BaseResponse.success(result);
    }
}
