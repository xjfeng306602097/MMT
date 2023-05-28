package com.makro.mall.product.controller.client;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.vo.ExcelDataFromSheetName;
import com.makro.mall.product.service.ImportV4Service;
import com.makro.mall.product.service.ProdListService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Ljj
 */
@Api(tags = "关联MM的商品列表", hidden = true)
@RestController
@RequestMapping("/api/v1/prod/list/client")
@RequiredArgsConstructor
public class ImportClientController {


    private final ImportV4Service ImportService;
    private final ProdListService prodListService;


    @PostMapping(value = "/importData/{mmCode}")
    public BaseResponse<Boolean> importData(@PathVariable String mmCode,
                                            @RequestBody ExcelDataFromSheetName excelData) {
        List<ProdList> prodListList = excelData.getTemplateDataVOList().stream().map(x -> {
            ProdList prodList = new ProdList();
            BeanUtil.copyProperties(x, prodList);
            return prodList;
        }).collect(Collectors.toList());
        ImportService.importData(mmCode, excelData.getInfo(),prodListList);
        return BaseResponse.success();
    }

    @PostMapping(value = "/remove/{mmCode}")
    public BaseResponse<Boolean> remove(@PathVariable String mmCode) {
        prodListService.remove(new LambdaQueryWrapper<ProdList>().eq(ProdList::getMmCode, mmCode));
        return BaseResponse.success();
    }
}
