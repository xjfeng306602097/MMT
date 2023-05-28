package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmStore;
import com.makro.mall.admin.service.MmStoreService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description MM Store
 * @date 2021/11/9
 */
@Api(tags = "MM Store接口")
@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class MmStoreController {

    private final MmStoreService mmStoreService;

    @ApiOperation(value = "列表查询-不分页")
    @GetMapping("/list")
    public BaseResponse<List<MmStore>> list(@ApiParam("编码") String code,
                                            @ApiParam("状态,1-上线,0-下线,默认1") Integer status,
                                            @ApiParam("名称") String name,
                                            @ApiParam("是否删除，1-是，0-否，默认0") Integer isDeleted) {
        return BaseResponse.success(mmStoreService.getList(code, status, name, isDeleted));
    }

    @ApiOperation(value = "门店详情")
    @GetMapping("/{id}")
    public BaseResponse detail(@ApiParam("活动id") @PathVariable Integer id) {
        return BaseResponse.success(mmStoreService.getById(id));
    }

    @ApiOperation(value = "新增store")
    @PostMapping
    public BaseResponse add(@Validated @RequestBody MmStore store) {
        return BaseResponse.judge(mmStoreService.save(store));
    }

    @ApiOperation(value = "修改store")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable Long id,
            @Validated @RequestBody MmStore store) {
        store.setId(id);
        return BaseResponse.judge(mmStoreService.updateById(store));
    }

    @ApiOperation(value = "删除store")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@ApiParam("id集合") @PathVariable String ids) {
        Boolean status = mmStoreService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList()));
        return BaseResponse.judge(status);
    }

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmStore>> page(@RequestBody SortPageRequest<MmStore> request) {
        String sortSql = request.getSortSql();
        MmStore req = request.getReq() != null ? request.getReq() : new MmStore();
        IPage<MmStore> result = mmStoreService.page(new MakroPage<>(request.getPage(), request.getLimit()), new LambdaQueryWrapper<MmStore>()
                .eq(StrUtil.isNotEmpty(req.getCode()), MmStore::getCode, req.getCode())
                .eq(req.getStatus() != null, MmStore::getStatus, req.getStatus())
                .like(StrUtil.isNotEmpty(request.getReq().getName()), MmStore::getName, StrUtil.trim(request.getReq().getName()))
                .last(StrUtil.isNotEmpty(sortSql), sortSql));
        return BaseResponse.success(result);
    }

}
