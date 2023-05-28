package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmTopic;
import com.makro.mall.admin.pojo.entity.MmTopicDetail;
import com.makro.mall.admin.service.MmTopicDetailService;
import com.makro.mall.admin.service.MmTopicService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 字典
 * @date 2022/1/30
 */
@Api(tags = "主题处理")
@RestController
@RequestMapping("/api/v1/topic")
@RequiredArgsConstructor
public class MmTopicController {

    private final MmTopicService mmTopicService;

    private final MmTopicDetailService mmTopicDetailService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmTopic>> list(@RequestBody SortPageRequest<MmTopic> request) {
        String sortSql = request.getSortSql();
        MmTopic req = request.getReq() != null ? request.getReq() : new MmTopic();
        IPage<MmTopic> result = mmTopicService.page(new MakroPage<>(request.getPage(), request.getLimit()), new LambdaQueryWrapper<MmTopic>()
                .eq(StrUtil.isNotEmpty(req.getCode()), MmTopic::getCode, req.getCode())
                .eq(req.getStatus() != null, MmTopic::getStatus, req.getStatus())
                .like(StrUtil.isNotEmpty(request.getReq().getName()), MmTopic::getName, StrUtil.trim(request.getReq().getName()))
                .last(StrUtil.isNotEmpty(sortSql), sortSql));
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "创建主题")
    @PostMapping
    public BaseResponse add(@RequestBody MmTopic topic) {
        return BaseResponse.success(mmTopicService.save(topic));
    }

    @ApiOperation(value = "詳情")
    @GetMapping("/{id}")
    public BaseResponse<MmTopic> getById(@PathVariable Long id) {
        return BaseResponse.success(mmTopicService.getById(id));
    }

    @ApiOperation(value = "获取内容")
    @GetMapping("/{id}/contents")
    public BaseResponse<List<MmTopicDetail>> getContents(@PathVariable Long id) {
        return BaseResponse.success(mmTopicDetailService.list(new LambdaQueryWrapper<MmTopicDetail>().eq(MmTopicDetail::getTopicId, id)));
    }

    @ApiOperation(value = "更新主题")
    @PutMapping("/{id}")
    public BaseResponse update(@PathVariable Long id, @RequestBody MmTopic topic) {
        topic.setId(id);
        return BaseResponse.judge(mmTopicService.updateById(topic));
    }

    @ApiOperation(value = "新增主题对话内容")
    @PostMapping("/{id}/contents")
    public BaseResponse addContent(@PathVariable Long id, @RequestBody MmTopicDetail topicDetail) {
        topicDetail.setId(id);
        return BaseResponse.judge(mmTopicDetailService.save(topicDetail));
    }

    @ApiOperation(value = "删除主题")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return BaseResponse.judge(mmTopicService.removeByIds(idList));
    }

    @ApiOperation(value = "删除主题内容")
    @DeleteMapping("/contents/{ids}")
    public BaseResponse deleteContents(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return BaseResponse.judge(mmTopicDetailService.removeByIds(idList));
    }

}
