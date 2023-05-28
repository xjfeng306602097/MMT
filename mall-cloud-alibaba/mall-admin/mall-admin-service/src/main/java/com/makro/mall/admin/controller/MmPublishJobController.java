package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmPublishJob;
import com.makro.mall.admin.service.MmPublishJobService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/5/6
 */
@Api(tags = "发布处理")
@RestController
@RequestMapping("/api/v1/publish/job")
@RequiredArgsConstructor
public class MmPublishJobController {

    private final MmPublishJobService mmPublishJobService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmPublishJob>> page(@RequestBody SortPageRequest<MmPublishJob> request) {
        String sortSql = request.getSortSql();
        MmPublishJob req = request.getReq() != null ? request.getReq() : new MmPublishJob();
        IPage<MmPublishJob> result = mmPublishJobService.page(new MakroPage<>(request.getPage(), request.getLimit()), new LambdaQueryWrapper<MmPublishJob>()
                .eq(StrUtil.isNotEmpty(req.getMmCode()), MmPublishJob::getMmCode, req.getMmCode())
                .eq(StrUtil.isNotEmpty(req.getMediaType()), MmPublishJob::getMediaType, req.getMediaType())
                .eq(req.getStatus() != null, MmPublishJob::getStatus, req.getStatus())
                .gt(req.getGmtCreateBegin() != null, MmPublishJob::getGmtCreate, req.getGmtCreateBegin())
                .lt(req.getGmtCreateEnd() != null, MmPublishJob::getGmtCreate, req.getGmtCreateEnd())
                .last(StrUtil.isNotEmpty(sortSql), sortSql));
        return BaseResponse.success(result.convert(MmPublishJob::convertFilePath));
    }

    @ApiOperation(value = "查看详情")
    @GetMapping("/{id}")
    public BaseResponse<MmPublishJob> detail(@PathVariable Long id) {
        return BaseResponse.success(Optional.of(mmPublishJobService.getById(id)).map(MmPublishJob::convertFilePath).orElse(null));
    }

    @ApiOperation(value = "根据流程id查看关联任务")
    @GetMapping("/{relatedFlow}/list")
    public BaseResponse<List<MmPublishJob>> listRelated(@PathVariable Long relatedFlow) {
        List<MmPublishJob> jobs = mmPublishJobService.list(new LambdaQueryWrapper<MmPublishJob>().eq(MmPublishJob::getRelatedFlow, relatedFlow));
        return BaseResponse.success(jobs.stream().map(MmPublishJob::convertFilePath).collect(Collectors.toList()));
    }

    @ApiOperation(value = "根据mmcode查看关联任务")
    @GetMapping("/mm/{mmCode}/list")
    public BaseResponse<List<MmPublishJob>> listRelated(@PathVariable String mmCode) {
        return BaseResponse.success(mmPublishJobService.listRelated(mmCode));
    }

    @ApiOperation(value = "批量创建job")
    @PostMapping("/{relatedFlow}/mm/{mmCode}")
    public BaseResponse<List<MmPublishJob>> saveBatch(@PathVariable(value = "relatedFlow") Long relatedFlow,
                                                      @PathVariable(value = "mmCode") String mmCode,
                                                      @RequestBody List<MmPublishJob> jobs) {
        return BaseResponse.success(mmPublishJobService.addBatch(relatedFlow, mmCode, jobs));
    }

    @ApiOperation(value = "修改job")
    @PutMapping(value = "/{id}")
    public BaseResponse update(@PathVariable Long id, @RequestBody MmPublishJob job) {
        return BaseResponse.judge(mmPublishJobService.updatePublishJob(id, job));
    }


}
