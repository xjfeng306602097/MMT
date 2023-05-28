package com.makro.mall.admin.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmSmsTextTemplate;
import com.makro.mall.admin.service.MmSmsTextTemplateService;
import com.makro.mall.common.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/11/10
 */
@Api(tags = "MM Sms Template接口")
@RestController
@RequestMapping("/api/v1/sms/template")
@RequiredArgsConstructor
public class MmSmsTemplateController {

    private final MmSmsTextTemplateService mmSmsTextTemplateService;

    @ApiOperation(value = "sms模板分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmSmsTextTemplate>> page(@RequestBody SortPageRequest<MmSmsTextTemplate> request) {
        String sortSql = request.getSortSql();
        MmSmsTextTemplate req = request.getReq() != null ? request.getReq() : new MmSmsTextTemplate();
        IPage<MmSmsTextTemplate> result = mmSmsTextTemplateService.page(new MakroPage<>(request.getPage(), request.getLimit()), new LambdaQueryWrapper<MmSmsTextTemplate>()
                .like(StrUtil.isNotEmpty(req.getTitle()), MmSmsTextTemplate::getTitle, req.getTitle())
                .eq(ObjectUtil.isNotNull(req.getStatus()), MmSmsTextTemplate::getStatus, req.getStatus())
                .last(StrUtil.isNotEmpty(sortSql), sortSql));
        return BaseResponse.success(result);
    }


    @ApiOperation(value = "sms模板详情")
    @GetMapping("/{id}")
    public BaseResponse<MmSmsTextTemplate> detail(@PathVariable Long id) {
        MmSmsTextTemplate template = mmSmsTextTemplateService.getById(id);
        return BaseResponse.success(template);
    }


    @ApiOperation(value = "新增sms模板")
    @PostMapping
    public BaseResponse<Boolean> create(@Validated @RequestBody MmSmsTextTemplate smsTextTemplate) {
        Assert.shortThan(smsTextTemplate.getTemplateContent(), 300, AdminStatusCode.TEMPLATE_CONTENT_CANNOT_EXCEED_300);
        return BaseResponse.judge(mmSmsTextTemplateService.save(smsTextTemplate));
    }

    @ApiOperation(value = "删除sms模板")
    @DeleteMapping("/{ids}")
    public BaseResponse<Boolean> delete(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
        return BaseResponse.judge(mmSmsTextTemplateService.removeByIds(idList));
    }

    @ApiOperation(value = "选择性更新sms模板")
    @PatchMapping(value = "/{id}")
    public BaseResponse<Boolean> patch(@PathVariable Long id, @RequestBody MmSmsTextTemplate mmSmsTextTemplate) {
        if (StrUtil.isNotBlank(mmSmsTextTemplate.getTemplateContent())) {
            Assert.shortThan(mmSmsTextTemplate.getTemplateContent(), 300, AdminStatusCode.TEMPLATE_CONTENT_CANNOT_EXCEED_300);
        }
        mmSmsTextTemplate.setId(id);
        return BaseResponse.success(mmSmsTextTemplateService.updateById(mmSmsTextTemplate));
    }

}
