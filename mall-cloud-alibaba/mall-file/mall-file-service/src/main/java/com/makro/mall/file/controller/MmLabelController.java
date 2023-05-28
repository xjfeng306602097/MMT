package com.makro.mall.file.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.file.pojo.entity.MmLabel;
import com.makro.mall.file.service.MmLabelService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description svg维护接口
 * @date 2021/10/26
 */
@Api(tags = "label维护接口")
@RestController
@RequestMapping("/api/v1/label")
@RequiredArgsConstructor
public class MmLabelController {

    private final MmLabelService mmLabelService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public BaseResponse<IPage<MmLabel>> pageList(@ApiParam("页码") Integer page,
                                                 @ApiParam("每页数量") Integer limit,
                                                 @ApiParam("图片名称") String name,
                                                 @ApiParam("类型") String type,
                                                 @ApiParam("状态") Integer status,
                                                 @ApiParam("类别") String classify) {
        LambdaQueryWrapper<MmLabel> queryWrapper = new LambdaQueryWrapper<MmLabel>()
                .eq(MmLabel::getDeleted, 0)
                .eq(StrUtil.isNotEmpty(type), MmLabel::getType, type)
                .eq(StrUtil.isNotEmpty(classify), MmLabel::getClassify, classify)
                .eq(status != null, MmLabel::getStatus, status)
                .like(StrUtil.isNotBlank(name), MmLabel::getName, name)
                .orderByDesc(MmLabel::getSort)
                .orderByDesc(MmLabel::getGmtModified)
                .orderByDesc(MmLabel::getGmtCreate);
        IPage<MmLabel> result = mmLabelService.page(new MakroPage<>(page, limit), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "label详情")
    @GetMapping("/{id}")
    public BaseResponse<MmLabel> detail(@PathVariable Long id) {
        return BaseResponse.success(mmLabelService.getById(id));
    }

    @ApiOperation(value = "新增label")
    @PostMapping
    public BaseResponse add(@RequestBody MmLabel label) {
        return BaseResponse.judge(mmLabelService.save(label));
    }

    @ApiOperation(value = "修改label")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmLabel label) {
        label.setId(id);
        return BaseResponse.judge(mmLabelService.updateById(label));
    }

    @ApiOperation(value = "删除label")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(mmLabelService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf)
                .collect(Collectors.toList())));
    }

}
