package com.makro.mall.file.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.file.pojo.entity.MmElement;
import com.makro.mall.file.pojo.vo.MmElementVO;
import com.makro.mall.file.service.MmElementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description Element控制器
 * @date 2021/10/26
 */
@Api(tags = "Element接口")
@RestController
@RequestMapping("/api/v1/element")
@RequiredArgsConstructor
public class MmElementController {

    private final MmElementService mmElementService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmElementVO>> pageList(@RequestBody SortPageRequest<MmElement> request) {
        String sortSql = request.getSortSql();
        MmElement req = request.getReq();
        LambdaQueryWrapper<MmElement> queryWrapper = new LambdaQueryWrapper<MmElement>()
                .eq(req.getDeleted() != null, MmElement::getDeleted, req.getDeleted())
                .eq(StrUtil.isNotEmpty(req.getType()), MmElement::getType, req.getType())
                .eq(req.getStatus() != null, MmElement::getStatus, req.getStatus())
                .like(StrUtil.isNotEmpty(req.getNameEn()), MmElement::getNameEn, req.getNameEn())
                .like(StrUtil.isNotEmpty(req.getNameThai()), MmElement::getNameThai, req.getNameThai())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<MmElement> result = mmElementService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        IPage<MmElementVO> resultList = result.convert(MmElementVO::convert);
        return BaseResponse.success(resultList);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<MmElementVO> detail(@PathVariable Long id) {
        return BaseResponse.success(MmElementVO.convert(mmElementService.getById(id)));
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @Transactional
    public BaseResponse add(@RequestBody @Validated MmElement element) {
        return BaseResponse.judge(mmElementService.save(element));
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @Transactional
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmElement element) {
        element.setId(id);
        return BaseResponse.judge(mmElementService.updateById(element));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(mmElementService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf)
                .collect(Collectors.toList())));
    }

}
