package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.SysDict;
import com.makro.mall.admin.pojo.entity.SysDictItem;
import com.makro.mall.admin.service.SysDictItemService;
import com.makro.mall.admin.service.SysDictService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 字典处理
 * @date 2021/10/18
 */
@Api(tags = "字典项处理")
@RestController
@Slf4j
@RequestMapping("/api/v1/dict-items")
@RequiredArgsConstructor
public class DictItemController {

    private final SysDictItemService sysDictItemService;

    private final SysDictService sysDictService;

    @ApiOperation(value = "列表分页")
    @GetMapping(value = "/page")
    public BaseResponse<IPage<SysDictItem>> page(Integer page, Integer limit, String name, String dictCode,
                                                 Long status, Integer parentId) {
        SysDictItem query = new SysDictItem();
        if (StrUtil.isNotEmpty(name)) {
            query.setName(name);
        }
        if (StrUtil.isNotEmpty(dictCode)) {
            query.setDictCode(dictCode);
        }
        if (parentId != null) {
            query.setParentId(parentId);
        }
        if (status != null) {
            query.setStatus(status);
        }
        IPage<SysDictItem> result = sysDictItemService.list(new MakroPage<>(page, limit),
                query);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "根据字典id获取所有字典项")
    @GetMapping(value = "/list/{dictId}")
    public BaseResponse<List<SysDictItem>> listByDictId(@PathVariable Integer dictId, Integer status, String name) {
        return BaseResponse.success(sysDictItemService.list(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getParentId, dictId)
                .eq(status != null, SysDictItem::getStatus, status)
                .like(StrUtil.isNotEmpty(name), SysDictItem::getName, name)
                .orderByAsc(SysDictItem::getSort)
                .orderByDesc(SysDictItem::getGmtCreate))
        );
    }

    @ApiOperation(value = "字典项列表")
    @GetMapping
    public BaseResponse list(String name, String dictCode, Integer status, String lang) {
        List<SysDictItem> list = sysDictItemService.list(
                new LambdaQueryWrapper<SysDictItem>()
                        .like(StrUtil.isNotBlank(name), SysDictItem::getName, name)
                        .eq(StrUtil.isNotBlank(dictCode), SysDictItem::getDictCode, dictCode)
                        .eq(status != null, SysDictItem::getStatus, status)
                        .orderByAsc(SysDictItem::getSort)
                        .orderByDesc(SysDictItem::getGmtCreate)
        );
        if (StrUtil.isNotEmpty(lang)) {
            List<SysDict> dicts = sysDictService.list(new LambdaQueryWrapper<SysDict>()
                    .in(SysDict::getCode, list.stream().map(SysDictItem::getDictCode).collect(Collectors.toList()))
                    .eq(SysDict::getLang, lang));
            Set<Integer> parentIds = dicts.stream().map(SysDict::getId).collect(Collectors.toSet());
            list = list.stream().filter(d -> parentIds.contains(d.getParentId())).collect(Collectors.toList());
        }
        return BaseResponse.success(list);
    }

    @ApiOperation(value = "字典项详情")
    @GetMapping(value = "/{id}")
    public BaseResponse<SysDictItem> detail(@PathVariable String id) {
        return BaseResponse.success(sysDictItemService.getById(id));
    }

    @ApiOperation(value = "新增字典项")
    @PostMapping
    public BaseResponse add(@ApiParam("实体JSON对象") @Validated @RequestBody SysDictItem dictItem) {
        if (dictItem.getDefaulted() != null && dictItem.getDefaulted() == 1) {
            List<SysDictItem> items = sysDictItemService.list(new LambdaQueryWrapper<SysDictItem>().eq(SysDictItem::getDictCode, dictItem.getDictCode()));
            items.stream().map(i -> {
                i.setDefaulted(0L);
                return i;
            }).collect(Collectors.toList());
            sysDictItemService.updateBatchById(items);
        }
        return BaseResponse.judge(sysDictItemService.save(dictItem));
    }

    @ApiOperation(value = "更新字典项")
    @PutMapping(value = "/{id}")
    public BaseResponse update(@ApiParam("实体JSON对象") @PathVariable Integer id, @RequestBody SysDictItem dictItem) {
        SysDictItem dbItem = sysDictItemService.getById(id);
        if (dictItem.getDefaulted() != null && dictItem.getDefaulted() == 1) {
            List<SysDictItem> items = sysDictItemService.list(new LambdaQueryWrapper<SysDictItem>().eq(SysDictItem::getParentId, dbItem.getParentId()));
            items.stream().map(i -> {
                if (!i.getId().equals(id)) {
                    i.setDefaulted(0L);
                }
                return i;
            }).collect(Collectors.toList());
            sysDictItemService.updateBatchById(items);
        }
        dictItem.setId(id);
        return BaseResponse.judge(sysDictItemService.updateById(dictItem));
    }

    @ApiOperation(value = "删除字典数据")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@ApiParam("主键ID集合，以,分割拼接字符串") @PathVariable String ids) {
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt)
                .collect(Collectors.toList());
        return BaseResponse.judge(sysDictItemService.removeByIds(idList));
    }

    @ApiOperation(value = "选择性更新字典数据")
    @PatchMapping(value = "/{id}")
    public BaseResponse patch(@ApiParam("用户ID") @PathVariable Integer id,
                              @ApiParam("实体JSON对象") @RequestBody SysDictItem dictItem) {
        LambdaUpdateWrapper<SysDictItem> updateWrapper = new LambdaUpdateWrapper<SysDictItem>().eq(SysDictItem::getId, id);
        updateWrapper.set(dictItem.getStatus() != null, SysDictItem::getStatus, dictItem.getStatus());
        return BaseResponse.judge(sysDictItemService.update(updateWrapper));
    }


}
