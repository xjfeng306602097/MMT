package com.makro.mall.admin.event.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.makro.mall.admin.common.constant.EmailConstants;
import com.makro.mall.admin.component.MmFlowContext;
import com.makro.mall.admin.event.pojo.MmFlowUpdateEvent;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmFlowConfigItem;
import com.makro.mall.admin.pojo.entity.SysUser;
import com.makro.mall.admin.pojo.entity.SysUserRole;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.admin.service.MmPublishJobService;
import com.makro.mall.admin.service.SysUserRoleService;
import com.makro.mall.admin.service.SysUserService;
import com.makro.mall.common.constants.GlobalConstants;
import com.makro.mall.message.api.MailMessageFeignClient;
import com.makro.mall.message.enums.MailTypeEnum;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.template.api.TemplateFeignClient;
import com.makro.mall.template.pojo.entity.MmTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 流程修改事务监听
 * @date 2022/4/6
 */
@Component
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class MmFlowUpdateListener implements EmailConstants {

    private static final String TEMPLATE_WORKFLOW = "TEMPLATE";

    private static final String MM_DESIGN_WORKFLOW = "MM-DESIGN";

    private static final String MM_PUBLISH_WORKFLOW = "MM-PUBLISH";

    private final MmFlowContext mmFlowContext;

    private final MmActivityService mmActivityService;

    private final TemplateFeignClient templateFeignClient;

    private final SysUserRoleService sysUserRoleService;

    private final SysUserService sysUserService;

    private final MailMessageFeignClient mailMessageFeignClient;

    private final MmPublishJobService mmPublishJobService;

    @Value("${workflow.redirect.url:https://dev-jc.makrogo.com/makroDigital/login?originUrl=/makroDigital/approvalProcess/myApproval}")
    private String redirectUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processConfigUpdate(MmFlowUpdateEvent event) {
        log.info("流程id-{}被修改,类型-{},更新数据内容-{}", event.getId(), event.getFlowType(), event);
        // 发送邮件
        if (StrUtil.isNotEmpty(event.getCurrentRole())) {
            noticeAuthUser(event);
        }
        if (event.isAutoApprove()) {
            changeAutoWorkflowBizStatus(event);
        } else {
            // 修改状态
            changeNormalWorkflowBizStatus(event);
        }
    }

    public void changeAutoWorkflowBizStatus(MmFlowUpdateEvent event) {
        switch (event.getFlowType()) {
            case TEMPLATE_WORKFLOW:
                MmTemplate template = new MmTemplate();
                template.setCode(event.getCode());
                template.setStatus(3);
                template.setApprovalInitiated(1L);
                templateFeignClient.updateUnlock(event.getCode(), template);
                log.info("流程id-{}被修改，触发template状态更新为-{}", event.getId(), 3);
                break;
            case MM_DESIGN_WORKFLOW:
                updateMmStatus(event, 4L);
                log.info("流程id-{}被修改，触发mm状态更新为-{}", event.getId(), 4);
                break;
            case MM_PUBLISH_WORKFLOW:
                updateMmStatus(event, 6L);
                updateFlowJobStatus(event, 1L);
                log.info("流程id-{}被修改，触发mm状态更新为-{}", event.getId(), 6);
                break;
            default:
                break;
        }
    }

    private void updateFlowJobStatus(MmFlowUpdateEvent event, Long status) {
        if (event.getFlowType().equals(MM_PUBLISH_WORKFLOW)) {
            mmPublishJobService.updateJobStatus(event.getId(), status);
        }
    }

    private void updateMmStatus(MmFlowUpdateEvent event, Long status) {
        MmActivity activity = new MmActivity();
        activity.setStatus(status);
        activity.setApprovalInitiated(1L);
        mmActivityService.update(activity, new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, event.getCode()));
    }

    public void changeNormalWorkflowBizStatus(MmFlowUpdateEvent event) {
        mmFlowContext.registerConfig(event.getConfigJson(), event.getFlowType());
        MmFlowConfigItem last = mmFlowContext.getLastPoint();
        MmFlowConfigItem first = mmFlowContext.getFirstPoint();
        switch (event.getFlowType()) {
            case TEMPLATE_WORKFLOW:
                updateTemplateStatus(event, last, first, 2, 1, 3);
                break;
            case MM_DESIGN_WORKFLOW:
                updateActivityStatus(event, last, first, 3L, 2L, 4L);
                break;
            case MM_PUBLISH_WORKFLOW:
                updateActivityStatus(event, last, first, 5L, 4L, 6L);
                break;
            default:
                break;
        }
        mmFlowContext.removeConfig();
    }

    private void noticeAuthUser(MmFlowUpdateEvent event) {
        List<String> roleCodes = new ArrayList<>();
        roleCodes.add(event.getCurrentRole());
        List<SysUserRole> sysUserRoles = sysUserRoleService.listSysUserRoleByCode(roleCodes);
        if (CollectionUtil.isNotEmpty(sysUserRoles)) {
            List<String> userIds = sysUserRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toList());
            List<SysUser> users = sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds)
                    .eq(SysUser::getStatus, GlobalConstants.STATUS_YES));
            MailMessage mailMessage = new MailMessage();
            mailMessage.setSuccessCount(0);
            mailMessage.setDelay(30L);
            mailMessage.setSubject("Please checkout the audit for Makro Mail System workflow");
            mailMessage.setToUser(users.stream().map(SysUser::getEmail).filter(StrUtil::isNotEmpty).toArray(String[]::new));
            MailMessage.H5MailInfo mailInfo = new MailMessage.H5MailInfo();
            mailInfo.setTemplateContent(HTML_BASE_TEMPLATE);
            mailInfo.setParams(Lists.newArrayList("Work Flow Notice", "Hi",
                    "You have got a new workflow to approve, please checkout",
                    String.format("<a href='%s'><Strong>Checkout Now</Strong></a>", redirectUrl),
                    String.valueOf(new Date())));
            mailMessage.setH5MailInfo(mailInfo);
            mailMessage.setMailTypeEnum(MailTypeEnum.H5_TEMPLATE);
            mailMessageFeignClient.add(mailMessage);
            log.info("成功发送邮件,邮件请求体{}", mailMessage);
        }
    }

    /**
     * @param event
     * @param last  最后一个流程
     * @param first 第一个流程
     * @param l     流程创建时对应的状态
     * @param l2    流程发起前的状态
     * @param l3    流程结束的状态
     */
    private void updateTemplateStatus(MmFlowUpdateEvent event, MmFlowConfigItem last, MmFlowConfigItem first,
                                      int l, int l2, int l3) {
        MmTemplate template = new MmTemplate();
        template.setCode(event.getCode());
        if (event.isCreate()) {
            template.setStatus(l);
        } else {
            // 如果是拒绝的情况，直接回退
            if (event.getStep().equals(first.getStep())) {
                template.setStatus(l2);
            } else if (event.getStep().equals(last.getStep())) {
                template.setStatus(l3);
            }
        }
        if (template.getStatus() != null) {
            // 修改template状态
            template.setApprovalInitiated(1L);
            templateFeignClient.updateUnlock(event.getCode(), template);
        }
        log.info("流程id-{}被修改，触发template状态刷新", event.getId());
    }

    /**
     * @param event
     * @param last  最后一个流程
     * @param first 第一个流程
     * @param l     流程创建时对应的状态
     * @param l2    流程发起前的状态
     * @param l3    流程结束的状态
     */
    private void updateActivityStatus(MmFlowUpdateEvent event, MmFlowConfigItem last, MmFlowConfigItem first,
                                      long l, long l2, long l3) {
        MmActivity activity = new MmActivity();
        if (event.isCreate()) {
            activity.setStatus(l);
        } else {
            // 如果是拒绝的情况，直接回退
            if (event.getStep().equals(first.getStep())) {
                activity.setStatus(l2);
                // 更新拒绝状态
                updateFlowJobStatus(event, 2L);
            } else if (event.getStep().equals(last.getStep())) {
                activity.setStatus(l3);
                // 更新通过状态
                updateFlowJobStatus(event, 1L);
            }
        }
        if (activity.getStatus() != null) {
            activity.setApprovalInitiated(1L);
            mmActivityService.update(activity, new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, event.getCode()));
        }
        log.info("流程id-{}被修改，触发MM状态刷新", event.getId());
    }

}
