package com.makro.mall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.admin.pojo.vo.MmSegmentVO;
import com.makro.mall.admin.service.MmSegmentService;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 卢嘉俊
 * @description SegmentAPI接口
 * @date 2022/5/16
 */
@Api(tags = "Segment接口")
@RestController
@RequestMapping("/api/v1/segments")
@Slf4j
@RequiredArgsConstructor
public class MmSegmentController {

    private final MmSegmentService mmSegmentService;

    @ApiOperation(value = "segment列表")
    @GetMapping("/list")
    public BaseResponse<List<MmSegment>> list(Long invalid) {
        return BaseResponse.success(mmSegmentService.list(new LambdaQueryWrapper<MmSegment>().eq(invalid != null, MmSegment::getInvalid, invalid).orderByAsc(MmSegment::getId)));
    }

    @ApiOperation(value = "segment分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmSegmentVO>> page(@RequestBody SortPageRequest<MmSegmentVO> request) {
        return BaseResponse.success(mmSegmentService.page(new MakroPage<>(request.getPage(), request.getLimit()), request.getReq(), request.getSortSql()));
    }


    @ApiOperation(value = "Segment详情")
    @GetMapping("/{id}")
    public BaseResponse<MmSegmentVO> detail(@ApiParam("SegmentID") @PathVariable Long id) {
        MmSegmentVO segment = mmSegmentService.getMmSegmentById(id);
        if (segment == null) {
            return BaseResponse.error(AdminStatusCode.SEGMENT_ID_IS_EMPTY);
        }
        return BaseResponse.success(segment);
    }


    @ApiOperation(value = "新增Segment")
    @PostMapping
    public BaseResponse<Boolean> create(@Validated @RequestBody MmSegmentVO segment) {
        return BaseResponse.judge(mmSegmentService.create(segment) > 0);
    }

    @ApiOperation(value = "删除Segment")
    @DeleteMapping("/{ids}")
    public BaseResponse<Boolean> delete(@PathVariable String ids) {
        return BaseResponse.judge(mmSegmentService.delete(ids));
    }

    @ApiOperation(value = "选择性更新Segment")
    @PatchMapping(value = "/{id}")
    public BaseResponse<Boolean> patch(@ApiParam("SegmentID") @PathVariable Long id, @RequestBody MmSegmentVO segmentVO) {
        segmentVO.setId(id);
        mmSegmentService.updateBySegmentVO(segmentVO);
        return BaseResponse.success();
    }

}
