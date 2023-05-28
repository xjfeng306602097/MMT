package com.makro.mall.product.controller;

import cn.hutool.core.bean.BeanUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.pojo.dto.ExcelDataDTO;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.vo.ProductVO;
import com.makro.mall.product.pojo.vo.SheetNameVo;
import com.makro.mall.product.service.ImportV4Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Api(tags = "MM商品导入接口 v4")
@RestController
@RequestMapping("/api/v1/import/v4")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class ImportV4Controller {


    private final ImportV4Service ImportService;

    @ApiOperation("1.上传Excel文件，并返回sheet列表，以及uploadID")
    @PostMapping(value = "/getSheetList")
    public BaseResponse<SheetNameVo> getSheetList(@RequestPart("file") MultipartFile file) {
        return BaseResponse.success(ImportService.getSheetList(file));
    }

    @ApiOperation("2.从/getSheetList获取文件流及其信息,根据uploadID和sheetName返回Excel数据")
    @PostMapping(value = "/getExcelDataFromSheetName/{uploadId}")
    public BaseResponse<ExcelDataDTO> getExcelDataFromSheetName(@PathVariable String uploadId,
                                                                @ApiParam("用户选择的sheetName") String sheetName) {
        return BaseResponse.success(ImportService.getExcelDataFromSheetName(uploadId, sheetName));
    }

    @ApiOperation("3.从/getExcelDataFromSheetName获取JSON,组装成ProductVO")
    @PostMapping(value = "/parseExcelData/toProductData")
    public BaseResponse<List<ProductVO>> toProductData(@RequestBody ExcelDataDTO dto) {
        List<ProdList> prodListList = dto.getDataList().stream().map(x -> {
            ProdList prodList = new ProdList();
            BeanUtil.copyProperties(x, prodList);
            return prodList;
        }).collect(Collectors.toList());
        return BaseResponse.success(ImportService.toProductData(prodListList));
    }


    @ApiOperation("4.导入第二步返回的数据/或其他数据")
    @PostMapping(value = "/addTemplateData/{mmCode}")
    public BaseResponse<Boolean> importData(@PathVariable String mmCode,
                                            @RequestBody ExcelDataDTO dto) {
        List<ProdList> prodListList = dto.getDataList().stream().map(x -> {
            ProdList prodList = new ProdList();
            BeanUtil.copyProperties(x, prodList);
            return prodList;
        }).collect(Collectors.toList());
        ImportService.importData(mmCode, dto.getInfo(), prodListList);
        return BaseResponse.success();
    }


    @ApiOperation("textthai表单,另存为excel")
    @PostMapping(value = "/form/{mmCode}")
    public BaseResponse<Boolean> form(@PathVariable String mmCode, @RequestBody List<ProdList> dto) {
        return BaseResponse.judge(ImportService.form(mmCode, dto));
    }

}
