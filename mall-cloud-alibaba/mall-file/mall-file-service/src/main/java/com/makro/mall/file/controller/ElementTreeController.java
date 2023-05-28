package com.makro.mall.file.controller;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.file.pojo.vo.ElementTreeListReqVO;
import com.makro.mall.file.pojo.vo.ElementTreeListRspVO;
import com.makro.mall.file.pojo.vo.ElementTreeUpDateReqVO;
import com.makro.mall.file.service.ElementTreeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 卢嘉俊
 * @Date: 2022/6/27 个人文件夹
 */
@Api(tags = "个人文件夹")
@RestController
@RequestMapping("/api/v1/elementFolder")
@RequiredArgsConstructor
public class ElementTreeController {

    private final ElementTreeService elementTreeService;

    @ApiOperation(value = "列表,初始为0")
    @PostMapping("/page")
    public BaseResponse<ElementTreeListRspVO> page(@Validated @RequestBody ElementTreeListReqVO vo) {
        return BaseResponse.success(elementTreeService.treeList(vo));
    }


    @ApiOperation(value = "新增")
    @PostMapping
    public BaseResponse<Boolean> add(@RequestBody List<ElementTreeUpDateReqVO> reqVO) {
        return BaseResponse.judge(elementTreeService.saveTree(reqVO));
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    public BaseResponse<Boolean> update(@PathVariable Long id, @RequestBody @Validated ElementTreeUpDateReqVO element) {
        element.setId(id);
        return BaseResponse.judge(elementTreeService.updateTree(element));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> delete(@PathVariable String ids) {
        return BaseResponse.judge(elementTreeService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList())));
    }

}
