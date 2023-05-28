package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.component.MmFlowContext;
import com.makro.mall.admin.event.pojo.MmFlowUpdateEvent;
import com.makro.mall.admin.pojo.dto.MmFlowCreateDTO;
import com.makro.mall.admin.pojo.entity.MmFlow;
import com.makro.mall.admin.pojo.entity.MmFlowConfigItem;
import com.makro.mall.admin.pojo.entity.MmFlowDetails;
import com.makro.mall.admin.pojo.vo.MmFlowDetailsVO;
import com.makro.mall.admin.service.MmFlowDetailsService;
import com.makro.mall.admin.service.MmFlowService;
import com.makro.mall.common.model.*;
import com.makro.mall.common.redis.lock.annotation.RedisLock;
import com.makro.mall.common.web.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 字典
 * @date 2022/1/30
 */
@Api(tags = "流程处理")
@RestController
@RequestMapping("/api/v1/flow")
@RequiredArgsConstructor
public class MmFlowController {

    private final MmFlowService mmFlowService;

    private final MmFlowContext mmFlowContext;

    private final MmFlowDetailsService mmFlowDetailsService;

    private final ApplicationEventPublisher publisher;

    @ApiOperation(value = "获取流程类型")
    @GetMapping("/types")
    public BaseResponse<List<Map<String, String>>> getFlows() {
        return BaseResponse.success(mmFlowContext.getFlows());
    }

    @ApiOperation(value = "流程按钮选项列")
    @GetMapping("/options")
    public BaseResponse<Set<String>> getOptions() {
        return BaseResponse.success(MmFlowDetails.OPTION_SET);
    }

    @ApiOperation(value = "获取对应流程的角色节点")
    @GetMapping("/{flowName}/roles")
    public BaseResponse<List<String>> getFlowRoles(@PathVariable(value = "flowName") String flowName) {
        return BaseResponse.success(mmFlowContext.getRolesByFlow(flowName));
    }

    @ApiOperation(value = "判断当前角色是否有权限完结流程")
    @GetMapping("/{id}/checkLast")
    public BaseResponse<Boolean> isLastPoint(@PathVariable(value = "id") Long id) {
        MmFlow flow = mmFlowService.getById(id);
        mmFlowContext.registerConfig(flow.getConfigJson(), flow.getType());
        boolean isTrue = mmFlowContext.containsLastStepRole();
        mmFlowContext.removeConfig();
        return BaseResponse.judge(isTrue);
    }

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmFlow>> page(@RequestBody SortPageRequest<MmFlow> request) {
        String sortSql = request.getSortSql();
        MmFlow req = request.getReq() != null ? request.getReq() : new MmFlow();
        IPage<MmFlow> result = mmFlowService.page(new MakroPage<>(request.getPage(), request.getLimit()), new LambdaQueryWrapper<MmFlow>()
                .eq(StrUtil.isNotEmpty(req.getCode()), MmFlow::getCode, req.getCode())
                .eq(StrUtil.isNotEmpty(req.getType()), MmFlow::getType, req.getType())
                .in(req.isJustMe(), MmFlow::getCurrentRole, JwtUtils.getRoles())
                .eq(req.getStatus() != null, MmFlow::getStatus, req.getStatus())
                .gt(req.getGmtCreateBegin() != null, MmFlow::getGmtCreate, req.getGmtCreateBegin())
                .lt(req.getGmtCreateEnd() != null, MmFlow::getGmtCreate, req.getGmtCreateEnd())
                .like(StrUtil.isNotEmpty(request.getReq().getName()), MmFlow::getName, StrUtil.trim(request.getReq().getName()))
                .like(StrUtil.isNotEmpty(request.getReq().getDescription()), MmFlow::getDescription, StrUtil.trim(request.getReq().getDescription()))
                .last(StrUtil.isNotEmpty(sortSql), sortSql));
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<MmFlow> getById(@PathVariable Long id) {
        return BaseResponse.success(mmFlowService.getById(id));
    }

    @ApiOperation(value = "流程节点明细列表")
    @GetMapping("/details/{code}")
    public BaseResponse<List<MmFlowDetails>> getByCode(@PathVariable String code) {
        List<MmFlowDetails> details = mmFlowDetailsService.list(new LambdaQueryWrapper<MmFlowDetails>().eq(MmFlowDetails::getCode, code)
                .orderByAsc(MmFlowDetails::getGmtCreate));
        details.forEach(MmFlowDetails::convertRemark);
        return BaseResponse.success(details);
    }

    @ApiOperation(value = "创建流程")
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse add(@RequestBody MmFlowCreateDTO flow) {
        MmFlow dbFlow = mmFlowService.getOne(new LambdaQueryWrapper<MmFlow>().eq(MmFlow::getCode, flow.getCode())
                .in(MmFlow::getStatus, MmFlow.STATUS_NEW, MmFlow.STATUS_IN_PROGRESS_APPROVE, MmFlow.STATUS_IN_PROGRESS_REJECT));
        Assert.isTrue(dbFlow == null, StatusCode.MM_FLOW_ONLY_EXISTS_ONE);
        Assert.isTrue(mmFlowContext.containsFlow(flow.getType()), StatusCode.MM_FLOW_NOT_EXISTS);
        if (mmFlowContext.isWorkflowOn(flow.getType())) {
            normalWorkflow(flow);
        } else {
            autoWorkflow(flow);
        }
        return BaseResponse.success(flow);
    }

    private void autoWorkflow(MmFlowCreateDTO flow) {
        // 走自动审核通过逻辑
        flow.setStatus(MmFlow.STATUS_AUTO_COMPLETED);
        mmFlowService.save(flow);
        // 记录创建记录
        MmFlowDetails details = new MmFlowDetails();
        details.setStepName("Auto Approve");
        details.setAuthUser(JwtUtils.getUsername());
        details.setFlowOption(MmFlowDetails.WORKFLOW_FINISHED);
        details.setCode(flow.getId().toString());
        details.setStep(1);
        details.setNextAuthRole(null);
        details.setLastAuthRole(null);
        details.setFilePath(flow.getFilePath());
        details.setRemark(flow.getHtmlRemark().getBytes(StandardCharsets.UTF_8));
        mmFlowDetailsService.save(details);
        MmFlowUpdateEvent event = new MmFlowUpdateEvent(flow.getId(), flow.getCode(), flow.getType(), true);
        publisher.publishEvent(event);
    }

    private void normalWorkflow(MmFlowCreateDTO flow) {
        MmFlowConfigItem item = mmFlowContext.getByStep(flow.getType(), 0);
        Assert.isTrue(mmFlowContext.hasPrivilege(item.getRoleCode()), StatusCode.MM_FLOW_FORBIDDEN);
        MmFlowConfigItem current = mmFlowContext.getByStep(flow.getType(), 1);
        String configJson = JSON.toJSONString(mmFlowContext.getConfigsByFlowName(flow.getType()));
        flow.setCurrentRole(current.getRoleCode());
        flow.setStep(current.getStep());
        flow.setStatus(MmFlow.STATUS_NEW);
        flow.setConfigJson(configJson);
        mmFlowService.save(flow);
        // 记录创建记录
        MmFlowDetails details = new MmFlowDetails();
        details.setStepName(item.getTitle());
        details.setAuthUser(JwtUtils.getUsername());
        details.setFlowOption(MmFlowDetails.FLOW_OPTION_CREATE);
        details.setCode(flow.getId().toString());
        details.setStep(1);
        details.setNextAuthRole(item.getRoleCode());
        details.setLastAuthRole(item.getRoleCode());
        details.setFilePath(flow.getFilePath());
        details.setRemark(flow.getHtmlRemark().getBytes(StandardCharsets.UTF_8));
        mmFlowDetailsService.save(details);
        MmFlowUpdateEvent event = new MmFlowUpdateEvent(flow.getId(), flow.getCode(), flow.getType(),
                1, configJson, true, current.getRoleCode());
        publisher.publishEvent(event);
    }

    @ApiOperation(value = "审核流程")
    @PutMapping("/verify")
    @Transactional(rollbackFor = Exception.class)
    @RedisLock(prefix = "mm:flow", key = "#details.code")
    public BaseResponse verify(@RequestBody MmFlowDetails details) {
        MmFlow dbFlow = mmFlowService.getById(details.getCode());
        // 找不到对应的流程
        Assert.isTrue(dbFlow != null, StatusCode.OBJECT_NOT_EXIST);
        // 流程已结束
        Assert.isTrue(!MmFlow.STATUS_COMPLETED.equals(dbFlow.getStatus())
                && !MmFlow.STATUS_IN_PROGRESS_CLOSED.equals(dbFlow.getStatus()), StatusCode.MM_FLOW_FINISHED);
        // 判断是否有权限授权
        Assert.isTrue(mmFlowContext.hasPrivilege(dbFlow.getCurrentRole()), StatusCode.MM_FLOW_FORBIDDEN);
        // 记录上一次审核的权限
        details.setLastAuthRole(dbFlow.getCurrentRole());
        details.setAuthUser(JwtUtils.getUsername());
        mmFlowContext.registerConfig(dbFlow.getConfigJson(), dbFlow.getType());
        Integer step = null;
        switch (details.getFlowOption()) {
            case MmFlowDetails.FLOW_OPTION_APPROVE:
                if (mmFlowContext.isLastPoint(dbFlow.getStep())) {
                    // 最后一个节点，认为通过
                    details.setNextAuthRole(null);
                    details.setStep(dbFlow.getStep());
                    details.setStepName(MmFlowDetails.WORKFLOW_FINISHED);
                    dbFlow.setStatus(MmFlow.STATUS_COMPLETED);
                } else {
                    // 流程不可指定某个用户直接跳过对应的节点进行下一步
                    MmFlowConfigItem item = mmFlowContext.getStep(dbFlow.getStep() + 1);
                    MmFlowConfigItem current = mmFlowContext.getStep(dbFlow.getStep());
                    details.setStepName(current.getTitle());
                    details.setNextAuthRole(item.getRoleCode());
                    details.setStep(current.getStep());
                    dbFlow.setStep(item.getStep());
                    dbFlow.setStatus(MmFlow.STATUS_IN_PROGRESS_APPROVE);
                }
                break;
            case MmFlowDetails.FLOW_OPTION_REJECT:
                MmFlowConfigItem item = null;
                MmFlowConfigItem current = mmFlowContext.getStep(dbFlow.getStep());
                if (details.getStep() == null) {
                    item = mmFlowContext.getStep(dbFlow.getStep() - 1);
                } else {
                    // 判断传入的流程是否合法
                    Assert.isTrue(details.getStep() < dbFlow.getStep(), StatusCode.ILLEGAL_STATE);
                    item = mmFlowContext.getStep(details.getStep());
                }
                details.setStep(current.getStep());
                details.setStepName(current.getTitle());
                details.setNextAuthRole(item.getRoleCode());
                dbFlow.setStep(item.getStep());
                if (item.getStep().equals(1)) {
                    // 回到最初的状态,流程自动结束
                    details.setStepName(MmFlowDetails.WORKFLOW_CLOSED);
                    dbFlow.setStatus(MmFlow.STATUS_IN_PROGRESS_CLOSED);
                } else {
                    dbFlow.setStatus(MmFlow.STATUS_IN_PROGRESS_REJECT);
                }
                step = item.getStep();
                break;
            case MmFlowDetails.FLOW_OPTION_NONE:
            default:
                break;
        }
        details.setRemark(details.getHtmlRemark().getBytes(StandardCharsets.UTF_8));
        mmFlowDetailsService.save(details);
        dbFlow.setCurrentRole(details.getNextAuthRole());
        dbFlow.setLastDetailId(details.getId());
        mmFlowContext.removeConfig();
        MmFlowUpdateEvent event = new MmFlowUpdateEvent(dbFlow.getId(), dbFlow.getCode(), dbFlow.getType(),
                step != null ? step : details.getStep(), dbFlow.getConfigJson(), false, dbFlow.getCurrentRole());
        publisher.publishEvent(event);
        return BaseResponse.judge(mmFlowService.updateById(dbFlow));
    }

    @ApiOperation(value = "删除流程")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        return BaseResponse.judge(mmFlowService.removeByIds(idList));
    }

    @ApiOperation(value = "获取模板或MMCode对应的流程详情")
    @GetMapping("/relate/{code}")
    public BaseResponse<MmFlowDetailsVO> getDetailsByCode(@ApiParam("对应的code，模板或MM的code") @PathVariable String code) {
        MmFlow flow = mmFlowService.getLastOne(code);
        Assert.isTrue(flow != null, StatusCode.NOT_FOUND);
        List<MmFlowDetails> details = mmFlowDetailsService.list(new LambdaQueryWrapper<MmFlowDetails>().eq(MmFlowDetails::getCode, flow.getId()).orderByAsc(MmFlowDetails::getGmtCreate));
        details.forEach(MmFlowDetails::convertRemark);
        MmFlowDetailsVO vo = new MmFlowDetailsVO(flow, details);
        return BaseResponse.success(vo);
    }

}
