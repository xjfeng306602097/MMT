package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.event.pojo.MmFlowConfigUpdateEvent;
import com.makro.mall.admin.pojo.entity.MmFlowConfig;
import com.makro.mall.admin.pojo.entity.MmFlowConfigItem;
import com.makro.mall.admin.service.MmFlowConfigItemService;
import com.makro.mall.admin.service.MmFlowConfigService;
import com.makro.mall.common.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/4/11
 */
@Api(tags = "MM流程配置")
@RestController
@RequestMapping("/api/v1/flow/config")
@RequiredArgsConstructor
public class MmFlowConfigController {

    private final MmFlowConfigService mmFlowConfigService;

    private final MmFlowConfigItemService mmFlowConfigItemService;

    private final ApplicationEventPublisher publisher;

    private static final Long DELETED_FLAG = 1L;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmFlowConfig>> list(@RequestBody SortPageRequest<MmFlowConfig> request) {
        String sortSql = request.getSortSql();
        MmFlowConfig req = request.getReq() != null ? request.getReq() : new MmFlowConfig();
        IPage<MmFlowConfig> result = mmFlowConfigService.page(new MakroPage<MmFlowConfig>(request.getPage(), request.getLimit()), new LambdaQueryWrapper<MmFlowConfig>()
                .eq(req.getStatus() != null, MmFlowConfig::getStatus, req.getStatus())
                .like(req.getCode() != null, MmFlowConfig::getCode, req.getCode())
                .like(StrUtil.isNotEmpty(request.getReq().getName()), MmFlowConfig::getName, StrUtil.trim(request.getReq().getName()))
                .last(StrUtil.isNotEmpty(sortSql), sortSql));
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "获取配置流程")
    @GetMapping("/{id}/items")
    public BaseResponse<List<MmFlowConfigItem>> listItems(@PathVariable Long id) {
        return BaseResponse.success(mmFlowConfigItemService.list(new LambdaQueryWrapper<MmFlowConfigItem>().eq(MmFlowConfigItem::getRelateConfig, id).orderByAsc(MmFlowConfigItem::getStep)));
    }

    @ApiOperation(value = "获取配置详情")
    @GetMapping("/{id}")
    public BaseResponse<MmFlowConfig> detail(@PathVariable Long id) {
        return BaseResponse.success(mmFlowConfigService.getById(id));
    }

    @ApiOperation(value = "修改配置")
    @PutMapping(value = "/{id}")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmFlowConfig config) {
        config.setId(id);
        MmFlowConfigUpdateEvent event = new MmFlowConfigUpdateEvent(id);
        publisher.publishEvent(event);
        return BaseResponse.judge(mmFlowConfigService.updateById(config));
    }

    @ApiOperation(value = "修改配置明细")
    @PutMapping(value = "/{id}/details")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse update(
            @PathVariable Long id,
            @Validated @RequestBody List<MmFlowConfigItem> itemList) {
        MmFlowConfig config = mmFlowConfigService.getById(id);
        List<MmFlowConfigItem> items = mmFlowConfigItemService.list(new LambdaQueryWrapper<MmFlowConfigItem>().eq(MmFlowConfigItem::getRelateConfig, id));
        Map<Integer, Long> map = items.stream().collect(Collectors.toMap(MmFlowConfigItem::getStep, MmFlowConfigItem::getId));
        Assert.isTrue(config != null, StatusCode.OBJECT_NOT_EXIST);
        // 修改流程配
        itemList.forEach(item -> {
            Long dbId = map.get(item.getStep());
            Assert.isTrue(dbId == null || dbId.equals(item.getId()), StatusCode.MM_FLOW_STEP_DUPLICATE);
            item.setRelateConfig(id);
            if (DELETED_FLAG.equals(item.getDeleted()) && item.getId() != null) {
                mmFlowConfigItemService.removeById(item.getId());
            } else {
                mmFlowConfigItemService.saveOrUpdate(item);
            }
        });
        MmFlowConfigUpdateEvent event = new MmFlowConfigUpdateEvent(id);
        publisher.publishEvent(event);
        return BaseResponse.success();
    }

    @ApiOperation(value = "删除流程配置节点")
    @DeleteMapping("/details/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        return BaseResponse.judge(mmFlowConfigItemService.removeByIds(idList));
    }


}
