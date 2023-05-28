package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.makro.mall.admin.common.constant.EmailConstants;
import com.makro.mall.admin.component.easyexcel.CustomerIdsLoader;
import com.makro.mall.admin.mapper.MmPublishJobEmailTaskMapper;
import com.makro.mall.admin.mq.producer.MmPublishJobEmailTaskProducer;
import com.makro.mall.admin.pojo.dto.BasePublishJobDTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobEmailTaskV2DTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.*;
import com.makro.mall.admin.pojo.vo.MmPublishJobEmailTaskRepVO;
import com.makro.mall.admin.service.*;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.common.util.AesBase62Util;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.message.api.MailMessageFeignClient;
import com.makro.mall.message.dto.MailSubscriptionFeignDTO;
import com.makro.mall.message.enums.MailTypeEnum;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.pojo.entity.MailSubscription;
import com.makro.mall.stat.pojo.api.ShortLinkFeignClient;
import com.makro.mall.stat.pojo.dto.ShortLinkGenerateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jincheng
 * @description 针对表【MM_PUBLISH_JOB_EMAIL_TASK】的数据库操作Service实现
 * @createDate 2022-06-01 15:49:18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MmPublishJobEmailTaskServiceImpl extends ServiceImpl<MmPublishJobEmailTaskMapper, MmPublishJobEmailTask> implements MmPublishJobEmailTaskService, CommonPublishJobTaskService {

    private final MmPublishJobService mmPublishJobService;
    private final MmCustomerService mmCustomerService;
    private final MmPublishJobEmailTaskProducer mmPublishJobEmailTaskProducer;
    private final MmActivityService mmActivityService;
    private final MmPublishJobTaskLogService mmPublishJobTaskLogService;
    private final MailMessageFeignClient mailMessageFeignClient;
    private final ShortLinkFeignClient shortLinkFeignClient;
    private final TransactionTemplate transactionTemplate;
    private final MmCustomerSegmentService mmCustomerSegmentService;
    private final MmCustomerMembertypeService mmCustomerMembertypeService;
    private final MmPublishJobFileService mmPublishJobFileService;

    @Autowired
    @Qualifier(value = "threadPoolTaskExecutor")
    private Executor threadPoolTaskExecutor;

    private final CustomerIdsLoader customerIdsLoader;
    @Value("${mail.unsubscribe.link}")
    private String unSubscribeLink;
    @Value("${mail.unsubscribe.salt:makro123}")
    private String salt;


    /**
     * @Author: 卢嘉俊
     * @Date: 2022/8/30 mm发布 处理黑白名单
     * 1.先从黑名单中排除白名单
     * 2.从所有用户中排除黑名单
     */
    private void listHandle(MmPublishJobEmailTask task, Set<String> realblackList) {
        String creator = JwtUtils.getUsername();
        List<String> customerIdList = StrUtil.splitTrim(task.getMmCustomerId(), ",");
        int splitNum = 500;
        List<List<String>> splitCustomer = Stream.iterate(0, n -> n + 1)
                .limit((customerIdList.size() + splitNum - 1) / splitNum)
                .parallel()
                .map(a -> customerIdList.parallelStream().skip((long) a * splitNum).limit(splitNum).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());
        CompletableFuture<Boolean>[] futures = new CompletableFuture[splitCustomer.size()];
        for (int i = 0; i < splitCustomer.size(); i++) {
            List<String> customerIds = splitCustomer.get(i);
            futures[i] = CompletableFuture.supplyAsync(() -> {
                List<MmCustomer> customers = mmCustomerService.list(new LambdaQueryWrapper<MmCustomer>().in(MmCustomer::getId, customerIds));
                List<MmPublishJobTaskLog> logs = customers.stream().filter(x -> !CollUtil.contains(realblackList, x.getCustomerCode())).map(x -> {
                    //处理黑白名单
                    MmPublishJobTaskLog mmPublishJobTaskLog = new MmPublishJobTaskLog();
                    mmPublishJobTaskLog.setTaskId(task.getId());
                    mmPublishJobTaskLog.setCustomerId(x.getId());
                    mmPublishJobTaskLog.setCustomerName(x.getName());
                    mmPublishJobTaskLog.setCustomerCode(x.getCustomerCode());
                    mmPublishJobTaskLog.setSendTo(x.getEmail());
                    mmPublishJobTaskLog.setStatus(MessageSendEnum.NOT_SENT.getStatus());
                    mmPublishJobTaskLog.setChannel("email");
                    mmPublishJobTaskLog.setCreator(creator);

                    //处理空白用户
                    if (StrUtil.isBlank(x.getEmail())) {
                        mmPublishJobTaskLog.setStatus(MessageSendEnum.FAILED.getStatus());
                    }
                    return mmPublishJobTaskLog;
                }).collect(Collectors.toList());

                Assert.isTrue(ObjectUtil.isNotEmpty(logs), AdminStatusCode.NO_SENDABLE_CUSTOMERS);

                //取消订阅
                List<MailSubscription> subscriptions = mailMessageFeignClient.subList(MailSubscriptionFeignDTO.builder().addresses(logs.stream().map(MmPublishJobTaskLog::getSendTo).collect(Collectors.toList()))
                        .status(MailSubscription.STATUS_OFF_LINE).build()).getData();
                List<String> filterList = CollectionUtil.isNotEmpty(subscriptions) ? subscriptions.stream().map(MailSubscription::getAddress).collect(Collectors.toList()) : Collections.emptyList();
                logs.forEach(x -> {
                    if (filterList.contains(x.getSendTo())) {
                        x.setStatus(MessageSendEnum.REJECTED.getStatus());
                        x.setSendTime(LocalDateTime.now());
                    }
                });

                mmPublishJobTaskLogService.saveBatch(logs);

                return true;
            }, threadPoolTaskExecutor).exceptionally(e -> {
                log.error("listHandle 处理异常 customerIds:{}", customerIds, e);
                return false;
            });
        }

        CompletableFuture.allOf(futures).whenComplete((r, e) -> {
            // 整体完成后，记录日志
            log.info("listHandle 完成推送任务批量处理");
            //检查是否存在发送用户
            long count = mmPublishJobTaskLogService.count(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                    .eq(MmPublishJobTaskLog::getChannel, "email")
                    .eq(MmPublishJobTaskLog::getTaskId, task.getId())
                    .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus()));
            Assert.isTrue(count > 0, AdminStatusCode.CUSTOMER_IS_EMPTY);
        }).join();


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanMmPublishJobTask() {
        List<MmPublishJobEmailTask> doList = Lists.newArrayList();
        List<MmPublishJobEmailTask> badList = Lists.newArrayList();
        //验证数据 校验原任务是否能发布
        List<MmPublishJobEmailTask> list = list(new LambdaQueryWrapper<MmPublishJobEmailTask>().eq(MmPublishJobEmailTask::getStatus, MessageTaskEnum.NOT_SENT.getStatus()));
        List<Long> collect = list.stream().map(MmPublishJobEmailTask::getMmPublishJobId).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return;
        }
        List<MmPublishJob> publishJobs = mmPublishJobService.list(new LambdaQueryWrapper<MmPublishJob>().in(MmPublishJob::getId, collect));
        publishJobs.forEach(mmPublishJob -> list.forEach(publishJobEmailTask -> {
            if (publishJobEmailTask.getMmPublishJobId().equals(mmPublishJob.getId())) {
                if (Objects.equals(mmPublishJob.getStatus(), 1L)) {
                    doList.add(publishJobEmailTask);
                    log.info("发布任务进入队列中{}", publishJobEmailTask);
                } else {
                    MmPublishJobEmailTask bad = new MmPublishJobEmailTask();
                    bad.setStatus(MessageTaskEnum.CANCELED.getStatus());
                    bad.setId(publishJobEmailTask.getId());
                    badList.add(publishJobEmailTask);
                    log.info("发布任务失败{}", publishJobEmailTask);
                }
            }
        }));

        //更改状态为失败
        updateBatchById(badList);
        //发布任务
        doList.forEach(x -> mmPublishJobEmailTaskProducer.sendMmPublishJobEmailTaskMessage(x.getId(), x.getWorkTime()));
    }


    @Override
    public Set<Long> getMmPublishTotal(String mmCode) {
        List<MmPublishJob> jobs = mmPublishJobService.list(
                new LambdaQueryWrapper<MmPublishJob>()
                        .select(MmPublishJob::getId).eq(MmPublishJob::getMmCode, mmCode)
                        .ne(MmPublishJob::getStatus, 2L));
        if (CollectionUtil.isEmpty(jobs)) {
            return Set.of();
        }
        List<Long> ids = jobs.stream().map(MmPublishJob::getId).collect(Collectors.toList());
        //查找所有joId对应发送任务
        List<MmPublishJobEmailTask> tasks = list(new LambdaQueryWrapper<MmPublishJobEmailTask>()
                .select(MmPublishJobEmailTask::getMmCustomerId)
                .in(MmPublishJobEmailTask::getMmPublishJobId, ids)
                .in(MmPublishJobEmailTask::getStatus, MessageTaskEnum.SUCCEEDED.getStatus(), MessageTaskEnum.PARTIALLY_SUCCEEDED.getStatus()));
        if (CollectionUtil.isEmpty(tasks)) {
            return Set.of();
        }
        Set<String> customerIds = tasks.stream().map(MmPublishJobEmailTask::getMmCustomerId).collect(Collectors.toSet());
        //统计客户名单
        Set<Long> result = new HashSet<>();
        customerIds.forEach(x -> {
            Set<Long> idSet = Arrays.stream(x.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            result.addAll(idSet);
        });
        return result;
    }

    @Override
    public IPage<MmPublishJobEmailTaskRepVO> page(SortPageRequest<MmPublishJobTaskReqDTO> req) {
        return getBaseMapper().page(new MakroPage<>(req.getPage(), req.getLimit()), req.getSortSql("a."), req.getReq()).convert(x -> {
            x.setPushTotal(StrUtil.splitTrim(x.getMmCustomerId(), ",").size());
            return x;
        });

    }

    @Override
    public void again(MmPublishJobTaskLog dto) {

        MmPublishJobEmailTask task = getById(dto.getTaskId());
        List<MmPublishJobTaskLog> logs = mmPublishJobTaskLogService.list(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                .eq(MmPublishJobTaskLog::getTaskId, dto.getTaskId())
                .eq(MmPublishJobTaskLog::getChannel, "email")
                .eq(MmPublishJobTaskLog::getCustomerId, dto.getCustomerId())
                .isNotNull(MmPublishJobTaskLog::getGmtModified)
                .orderByDesc(MmPublishJobTaskLog::getGmtModified)
        );
        //1.30s内只能发送一次
        MmPublishJobTaskLog sent = logs.stream().findFirst().orElse(new MmPublishJobTaskLog());
        boolean isDoPublish = LocalDateTimeUtil.between(sent.getGmtModified(), LocalDateTimeUtil.now(), ChronoUnit.SECONDS) > 30L;
        Assert.isTrue(isDoPublish, AdminStatusCode.CAN_ONLY_BE_SENT_ONCE_IN_30S);

        //2.退订不发送
        MmPublishJobTaskLog log = new MmPublishJobTaskLog();
        BeanUtil.copyProperties(sent, log);
        MailSubscriptionFeignDTO mailSubscriptionFeignDTO = new MailSubscriptionFeignDTO();
        mailSubscriptionFeignDTO.setAddresses(List.of(sent.getSendTo()));
        mailSubscriptionFeignDTO.setStatus(MailSubscription.STATUS_OFF_LINE);
        List<MailSubscription> subscriptions = mailMessageFeignClient.subList(mailSubscriptionFeignDTO).getData();
        log.setCreator(null);
        log.setGmtCreate(null);
        log.setGmtModified(null);
        log.setSendTime(null);
        log.setId(null);
        if (CollUtil.isNotEmpty(subscriptions)) {
            log.setStatus(MessageSendEnum.REJECTED.getStatus());
            log.setSendTime(LocalDateTime.now());
        } else {
            log.setStatus(MessageSendEnum.NOT_SENT.getStatus());
        }
        transactionTemplate.execute(x -> {
            mmPublishJobTaskLogService.save(log);
            send(task, log);
            return null;
        });
    }

    @Override
    public void send(MmPublishJobEmailTask task, MmPublishJobTaskLog customer) {
        MailMessage mailMessage = new MailMessage();
        mailMessage.setToUser(new String[]{customer.getSendTo()});
        mailMessage.setSubject(task.getSubject());
        mailMessage.setMailTypeEnum(MailTypeEnum.H5_TEMPLATE);
        MailMessage.H5MailInfo mailInfo = new MailMessage.H5MailInfo();
        String content = shortLink(task, customer.getCustomerId());
        content = parseUnsubscribe(content, customer.getSendTo());
        mailInfo.setTemplateContent(content);
        mailInfo.setParams(Lists.newArrayList());
        mailMessage.setH5MailInfo(mailInfo);
        mailMessage.setSuccessCount(0);
        mailMessage.setMmPublishJobEmailTaskId(task.getId());
        mailMessage.setCustomerId(customer.getCustomerId());
        mailMessageFeignClient.add(mailMessage);
    }

    @Override
    public void publishV2(MmPublishJobEmailTaskV2DTO publishJobTaskDTO) throws IOException {
        MmPublishJob publishJob = mmPublishJobService.getById(publishJobTaskDTO.getJobId());
        MmActivity activity = mmActivityService.getOne(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, publishJob.getMmCode()));

        //校验参数
        Assert.notNull(publishJob, AdminStatusCode.MMPUBLISHJOB_IS_EMPTY);
        Assert.isTrue(Objects.equals(publishJob.getStatus(), 1L), AdminStatusCode.MMPUBLISHJOB_REVIEW_FAILED);
        Assert.isTrue(Objects.equals(publishJob.getSendByEmail(), 1L), AdminStatusCode.THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_EMAIL);
        Assert.isTrue(publishJobTaskDTO.getWorkTime().isAfter(LocalDateTime.now()), AdminStatusCode.WORK_TIME_IS_BEFORE_NOW);

        //设置mm
        publishJobTaskDTO.setMmCode(publishJob.getMmCode());

        //设置模板
        if (ObjectUtil.isNull(publishJobTaskDTO.getTemplate())) {
            publishJobTaskDTO.setTemplate(EmailConstants.HTML_MM_PUBLISH_JOB_EMAIL_TEMPLATE);
        }
        //设置预览图
        if (StrUtil.isEmpty(publishJobTaskDTO.getReviewUrl())) {
            publishJobTaskDTO.setReviewUrl(activity.getPreviewUrl());
        }
        //设置发布地址
        String storeCode = publishJobTaskDTO.getStoreCode() == null ? activity.getStoreCode() + "" : publishJobTaskDTO.getStoreCode() + "";
        String pageNo = publishJobTaskDTO.getPageNo() == null ? "" : publishJobTaskDTO.getPageNo() + "";
        String publishUrl = publishJob.getFilePath() + "?c=${c}&p=" + pageNo + "&q=email&s=" + storeCode;
        publishJobTaskDTO.setPublishUrl(publishUrl);

        publishJobTaskDTO.setTemplate(publishJobTaskDTO.getTemplate()
                .replace("${img}", publishJobTaskDTO.getReviewUrl()));

        // 过滤用户
        BasePublishJobDTO.PublishCondition sendList = publishJobTaskDTO.getSendList();
        BasePublishJobDTO.PublishCondition exceptList = publishJobTaskDTO.getExceptList();
        Set<String> whiteList = new HashSet<>();
        Set<String> blackList = new HashSet<>();
        if (ObjectUtil.isNotNull(sendList)) {
            // 根据segments查出对应的customerIds
            Set<Long> sendCustomerIdsBySegmentIds = mmCustomerSegmentService.getCustomerIdsBySegmentIds(sendList.getSegmentIds());
            Set<Long> exceptCustomerIdsBySegmentIds = mmCustomerSegmentService.getCustomerIdsBySegmentIds(exceptList.getSegmentIds());
            Set<Long> sendCustomerIdsByMemberTypeIds = mmCustomerMembertypeService.getCustomerIdsByMemberTypeIds(sendList.getMemberTypeIds());
            Set<Long> exceptCustomerIdsByMemberTypeIds = mmCustomerMembertypeService.getCustomerIdsByMemberTypeIds(exceptList.getMemberTypeIds());

            // 通过s3初始化加载对应的客户列表
            whiteList = customerIdsLoader.getCustomerIdsFromExcel(sendList.getCustomersS3Url());
            blackList = customerIdsLoader.getCustomerIdsFromExcel(exceptList.getCustomersS3Url());

            // 合并
            Set<Long> send = CollUtil.unionDistinct(sendCustomerIdsBySegmentIds, sendCustomerIdsByMemberTypeIds);
            Set<Long> except = CollUtil.unionDistinct(exceptCustomerIdsBySegmentIds, exceptCustomerIdsByMemberTypeIds);
            publishJobTaskDTO.setMmCustomerId((Set<Long>) CollUtil.subtract(send, except));
            Assert.isTrue(CollUtil.isNotEmpty(publishJobTaskDTO.getMmCustomerId()), AdminStatusCode.CUSTOMER_IS_EMPTY);
        }

        // 实际的黑名单
        Set<String> realBlackList = (Set<String>) CollUtil.subtract(blackList, whiteList);


        MmPublishJobEmailTask publishJobTask = MmPublishJobEmailTaskV2DTO.toNotSent(publishJobTaskDTO);
        transactionTemplate.execute(x -> {
            save(publishJobTask);
            ////保存黑白名单地址表
            //saveFileUrl(sendList, publishJobTask.getId(), "whiteList");
            //saveFileUrl(exceptList, publishJobTask.getId(), "blackList");
            //记录任务日志 黑白名单处理
            listHandle(publishJobTask, realBlackList);
            return null;
        });
        //24小时>执行时间 直接推消息队列
        boolean isDoPublish = LocalDateTimeUtil.of(DateUtil.tomorrow()).isAfter(publishJobTaskDTO.getWorkTime());
        if (isDoPublish) {
            mmPublishJobEmailTaskProducer.sendMmPublishJobEmailTaskMessage(publishJobTask.getId(), publishJobTask.getWorkTime());
        }
    }

    private void saveFileUrl(BasePublishJobDTO.PublishCondition list, String taskId, String fileType) {
        if (ObjectUtil.isNotNull(list) && StrUtil.isNotBlank(list.getCustomersS3Url())) {
            MmPublishJobFile mmPublishJobFile = new MmPublishJobFile()
                    .setMmPublishJobId(taskId)
                    .setFileUrl(list.getCustomersS3Url())
                    .setFileType(fileType)
                    .setChannel("email");
            mmPublishJobFileService.save(mmPublishJobFile);
        }
    }

    private String parseUnsubscribe(String content, String email) {
        String digest = MD5.create().digestHex(email + salt);
        String link = unSubscribeLink + "?address=" + email + "&d=" + digest;
        return content.replace("${unsubscribeUrl}", link).replace("${email}", email);
    }


    /**
     * 功能描述: 制作发布内容的短链 并拼接模板
     *
     * @Author: 卢嘉俊
     * @Date: 2022/9/13 mm发布 短链
     */
    public String shortLink(MmPublishJobEmailTask task, Long id) {
        String url = task.getPublishUrl().replace("${c}", AesBase62Util.encode(id));
        String shortLink = shortLinkFeignClient.shortLink(new ShortLinkGenerateDTO(url, null)).getMsg();
        return task.getTemplate().replace("${publishUrl}", shortLink);
    }


}




