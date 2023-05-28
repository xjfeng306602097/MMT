package com.makro.mall.file.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.file.pojo.entity.MmPdfSize;
import com.makro.mall.file.service.MmPdfSizeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 规格控制器
 * @date 2021/10/26
 */
@Api(tags = "Pdf Size接口")
@RestController
@RequestMapping("/api/v1/pdf/size")
@RequiredArgsConstructor
public class MmPdfSizeController {

    private final MmPdfSizeService mmPdfSizeService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmPdfSize>> pageList(@RequestBody SortPageRequest<MmPdfSize> request) {
        String sortSql = request.getSortSql();
        MmPdfSize req = request.getReq();
        LambdaQueryWrapper<MmPdfSize> queryWrapper = new LambdaQueryWrapper<MmPdfSize>()
                .eq(MmPdfSize::getDeleted, 0)
                .eq(req.getSizeRate() != null, MmPdfSize::getSizeRate, req.getSizeRate())
                .like(StrUtil.isNotBlank(req.getName()), MmPdfSize::getName, req.getName())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<MmPdfSize> result = mmPdfSizeService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "根据比例查询")
    @PostMapping("/listByRate/{rate}")
    public BaseResponse<List<MmPdfSize>> listByRate(@PathVariable BigDecimal rate) {
        return BaseResponse.success(mmPdfSizeService.list(new LambdaQueryWrapper<MmPdfSize>().eq(MmPdfSize::getSizeRate, rate)));
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<MmPdfSize> detail(@PathVariable Long id) {
        return BaseResponse.success(mmPdfSizeService.getById(id));
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public BaseResponse add(@RequestBody MmPdfSize size) {
        if (size.getWidth() != null && size.getHeight() != null) {
            size.setSizeRate(size.getWidth().divide(size.getHeight(), 2, RoundingMode.HALF_UP));
        }
        return BaseResponse.judge(mmPdfSizeService.save(size));
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmPdfSize size) {
        size.setId(id);
        if (size.getWidth() != null && size.getHeight() != null) {
            size.setSizeRate(size.getWidth().divide(size.getHeight(), 2, RoundingMode.HALF_UP));
        }
        return BaseResponse.judge(mmPdfSizeService.updateById(size));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(mmPdfSizeService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf)
                .collect(Collectors.toList())));
    }

}
