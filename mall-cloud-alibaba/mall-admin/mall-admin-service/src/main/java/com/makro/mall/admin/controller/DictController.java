package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.SysDict;
import com.makro.mall.admin.pojo.entity.SysDictItem;
import com.makro.mall.admin.service.SysDictItemService;
import com.makro.mall.admin.service.SysDictService;
import com.makro.mall.common.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 字典
 * @date 2021/10/18
 */
@Api(tags = "字典处理")
@RestController
@RequestMapping("/api/v1/dicts")
@RequiredArgsConstructor
public class DictController {

    private final SysDictService sysDictService;
    private final SysDictItemService sysDictItemService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<SysDict>> list(@RequestBody SortPageRequest<SysDict> request) {
        String sortSql = request.getSortSql();
        SysDict req = request.getReq() != null ? request.getReq() : new SysDict();
        IPage<SysDict> result = sysDictService.page(new MakroPage<>(request.getPage(), request.getLimit()), new LambdaQueryWrapper<SysDict>()
                .in(StrUtil.isNotEmpty(req.getLang()), SysDict::getLang, req.getLang().split(","))
                .eq(StrUtil.isNotEmpty(req.getCode()), SysDict::getCode, req.getCode())
                .eq(req.getStatus() != null, SysDict::getStatus, req.getStatus())
                .like(StrUtil.isNotEmpty(request.getReq().getName()), SysDict::getName, StrUtil.trim(request.getReq().getName()))
                .last(StrUtil.isNotEmpty(sortSql), sortSql));
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "字典项列表")
    @GetMapping
    public BaseResponse list() {
        List<SysDict> list = sysDictService.list(new LambdaQueryWrapper<SysDict>()
                .orderByDesc(SysDict::getGmtModified)
                .orderByDesc(SysDict::getGmtModified));
        return BaseResponse.success(list);
    }

    @ApiOperation(value = "字典详情")
    @GetMapping("/{id}")
    public BaseResponse<SysDict> detail(@ApiParam("字典id") @PathVariable Integer id) {
        SysDict dict = sysDictService.getById(id);
        return BaseResponse.success(dict);
    }

    @ApiOperation(value = "新增字典")
    @PostMapping
    public BaseResponse add(@ApiParam("实体JSON对象") @RequestBody SysDict dict) {
        SysDict dbDict = sysDictService.getOne(new LambdaQueryWrapper<SysDict>().eq(SysDict::getCode, dict.getCode())
                .eq(SysDict::getLang, dict.getLang()));
        Assert.isTrue(dbDict == null, StatusCode.DICT_CODE_DUPLICATE);
        dict.setStatus(dict.getStatus() != null ? dict.getStatus() : 1);
        return BaseResponse.judge(sysDictService.save(dict));
    }

    @ApiOperation(value = "修改字典")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @ApiParam("字典id") @PathVariable Integer id,
            @ApiParam("实体JSON对象") @RequestBody SysDict dict) {
        SysDict dbDict = sysDictService.getById(id);
        dict.setId(id);
        // 判断是否存在同code同lang的字典
        if (StrUtil.isNotEmpty(dict.getCode()) || StrUtil.isNotEmpty(dict.getLang())) {
            SysDict sameDict = sysDictService.getOne(new LambdaQueryWrapper<SysDict>()
                    .eq(SysDict::getCode, StrUtil.isNotEmpty(dict.getCode()) ? dict.getCode() : dbDict.getCode())
                    .eq(SysDict::getLang, StrUtil.isNotEmpty(dict.getLang()) ? dict.getLang() : dbDict.getLang())
                    .ne(SysDict::getId, id)
            );
            Assert.isTrue(sameDict == null, StatusCode.DICT_CODE_DUPLICATE);
        }
        Boolean status = sysDictService.updateById(dict);
        if (status) {
            if (StrUtil.isNotEmpty(dict.getCode()) && !StrUtil.equals(dbDict.getCode(), dict.getCode())) {
                sysDictItemService.update(new LambdaUpdateWrapper<SysDictItem>().eq(SysDictItem::getParentId,
                        dbDict.getId()).set(SysDictItem::getDictCode, dict.getCode()));
            }
        }
        return BaseResponse.judge(status);
    }

    @ApiOperation(value = "删除字典")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@ApiParam("以,分割拼接字符串") @PathVariable String ids) {
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        return BaseResponse.judge(sysDictService.batchDelete(idList));
    }

}
