package com.makro.mall.product.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.product.pojo.entity.ProdAux;
import com.makro.mall.product.service.ProdAuxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:商品附加属性
 * @Author: zhuangzikai
 * @Date: 2021/11/14
 **/
@Api(tags = "商品附加属性接口")
@RestController
@RequestMapping("/api/v1/product/aux")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class AuxController {
    private final ProdAuxService auxService;

    @ApiOperation(value = "商品附加属性分页")
    @GetMapping
    public BaseResponse<IPage<ProdAux>> list(@ApiParam("页码") Integer page,
                                             @ApiParam("每页数量") Integer limit,
                                             @ApiParam("类别") String type,
                                             @ApiParam("1：默认，0：其他") Integer isvalid) {
        ProdAux aux = new ProdAux();
        aux.setType(type);
        if (isvalid != null) {
            aux.setIsvalid(isvalid);
        }
        IPage<ProdAux> result = auxService.list(new MakroPage<>(page, limit), aux);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "新增商品附加属性")
    @PostMapping(value = "/add")
    public BaseResponse add(@RequestBody ProdAux aux) {
        ProdAux auxExist = new ProdAux();
        auxExist.setType(aux.getType());
        auxExist.setTitle(aux.getTitle());
        auxExist.setValue(aux.getValue());
        // 判断 type + title + value 是否存在，不存在则新增，存在则否。
        IPage<ProdAux> result = auxService.list(new MakroPage<>(1, 1), auxExist);
        Long total = result.getTotal();
        Boolean saveResult = true;
        String message = "success";
        if (total == 0) {
            aux.setId(IdUtil.simpleUUID());
            aux.setCreator(JwtUtils.getUsername());
            aux.setIsvalid(1);
            saveResult = auxService.save(aux);
        } else {
            saveResult = false;
            message = "Duplicate Record.";
        }
        return BaseResponse.judge(saveResult, message);
    }

    @ApiOperation(value = "商品附加属性详情")
    @GetMapping("/{id}")
    public BaseResponse details(@PathVariable String id) {
        ProdAux aux = auxService.getById(id);
        return BaseResponse.success(aux);
    }

    @ApiOperation(value = "更新商品附加属性")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public BaseResponse update(@PathVariable String id, @RequestBody ProdAux aux) {
        aux.setId(id);
        Boolean result = auxService.updateById(aux);
        return BaseResponse.judge(result);
    }

    @ApiOperation(value = "删除商品附加属性")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        Boolean status = auxService.removeByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        return BaseResponse.judge(status);
    }

    @ApiOperation(value = "商品附加属性类型列表")
    @RequestMapping(value = "/getTypes", method = RequestMethod.GET)
    public BaseResponse getAuxType() {
        List<String> list = auxService.getAuxType();
        return BaseResponse.success(list, "");
    }
}
