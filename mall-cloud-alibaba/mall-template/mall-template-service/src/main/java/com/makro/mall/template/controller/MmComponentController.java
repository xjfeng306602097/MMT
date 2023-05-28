package com.makro.mall.template.controller;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.template.pojo.entity.MmComponent;
import com.makro.mall.template.pojo.entity.MmComponentDraft;
import com.makro.mall.template.service.MmComponentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 组件控制器
 * @date 2021/10/27
 */
@Api(tags = "组件相关接口")
@RestController
@RequestMapping("/api/v1/component")
@RequiredArgsConstructor
public class MmComponentController {

    private final MmComponentService mmComponentService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public BaseResponse<Page<MmComponent>> pageList(@ApiParam("页码") Integer page,
                                                    @ApiParam("每页数量") Integer limit,
                                                    @ApiParam("起始时间") Date begin,
                                                    @ApiParam("终止时间") Date end,
                                                    @ApiParam("状态,用,隔开") String status,
                                                    @ApiParam("名称") String name,
                                                    @ApiParam("是否删除") Integer isDelete) {
        List<Integer> statusList = new ArrayList<>();
        if (StrUtil.isNotBlank(status)) {
            int[] arrays = Arrays.stream(status.split(", ")).mapToInt(Integer::parseInt).toArray();
            statusList = Arrays.stream(arrays).boxed().collect(Collectors.toList());
        }
        isDelete = Optional.ofNullable(isDelete).orElse(0);
        return BaseResponse.success(mmComponentService.page(statusList, name, begin, end, page, limit, isDelete));
    }

    @ApiOperation(value = "组件详情")
    @GetMapping("/{code}")
    public BaseResponse<MmComponent> detail(@ApiParam("组件code") @PathVariable String code) {
        return BaseResponse.success(mmComponentService.getByCode(code));
    }

    @ApiOperation(value = "新增组件")
    @PostMapping
    public BaseResponse add(@RequestBody MmComponent component) {
        return BaseResponse.success(mmComponentService.save(component));
    }

    @ApiOperation(value = "修改组件")
    @PutMapping(value = "/{code}")
    public BaseResponse update(
            @ApiParam("组件code") @PathVariable String code,
            @RequestBody MmComponent component) {
        component.setCode(code);
        return BaseResponse.success(mmComponentService.update(component));
    }

    @ApiOperation(value = "删除组件,状态由1改为0")
    @DeleteMapping("/{codes}")
    public BaseResponse delete(@ApiParam("code集合,用,隔开") @PathVariable String codes) {
        return BaseResponse.success(mmComponentService.removeByCodes(Arrays.stream(codes.split(","))
                .collect(Collectors.toList())));
    }

    @ApiOperation(value = "历史内容")
    @GetMapping("/{code}/draft/{version}")
    public BaseResponse<MmComponentDraft> draft(@ApiParam("模板code") @PathVariable(value = "code") String code,
                                                @ApiParam("版本号") @PathVariable(value = "version") Integer version) {
        return BaseResponse.success(mmComponentService.getDraft(code, version));
    }

    @ApiOperation(value = "历史版本内容")
    @GetMapping("/{code}/draft")
    public BaseResponse getDraftInfos(@ApiParam("模板code") @PathVariable(value = "code") String code) {
        return BaseResponse.success(mmComponentService.getDraftInfos(code));
    }


}
