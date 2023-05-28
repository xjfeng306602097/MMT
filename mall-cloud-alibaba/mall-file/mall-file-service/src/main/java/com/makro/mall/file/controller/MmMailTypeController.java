package com.makro.mall.file.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.file.pojo.entity.MmMailType;
import com.makro.mall.file.service.MmMailTypeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 字体控制器
 * @date 2021/10/26
 */
@Api(tags = "MailType接口")
@RestController
@RequestMapping("/api/v1/mailType")
@RequiredArgsConstructor
public class MmMailTypeController {

    private final MmMailTypeService mmMailTypeService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public BaseResponse<IPage<MmMailType>> pageList(@ApiParam("页码") Integer page,
                                                    @ApiParam("每页数量") Integer limit,
                                                    @ApiParam("字体名称") String name) {
        LambdaQueryWrapper<MmMailType> queryWrapper = new LambdaQueryWrapper<MmMailType>()
                .eq(MmMailType::getDeleted, 0)
                .like(StrUtil.isNotBlank(name), MmMailType::getName, name)
                .orderByDesc(MmMailType::getGmtModified)
                .orderByDesc(MmMailType::getGmtCreate);
        IPage<MmMailType> result = mmMailTypeService.page(new MakroPage<>(page, limit), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<MmMailType> detail(@ApiParam("字体id") @PathVariable Long id) {
        return BaseResponse.success(mmMailTypeService.getById(id));
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public BaseResponse add(@RequestBody MmMailType type) {
        return BaseResponse.judge(mmMailTypeService.save(type));
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmMailType type) {
        type.setId(id);
        return BaseResponse.judge(mmMailTypeService.updateById(type));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(mmMailTypeService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf)
                .collect(Collectors.toList())));
    }

}
