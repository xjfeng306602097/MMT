package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.makro.mall.admin.component.easyexcel.CustomerIdsLoader;
import com.makro.mall.admin.mapper.MmPublishJobMapper;
import com.makro.mall.admin.mapper.MmPublishJobSmsTaskMapper;
import com.makro.mall.admin.mq.producer.MmPublishJobSmsTaskProducer;
import com.makro.mall.admin.pojo.dto.BasePublishJobDTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobSmsTaskV2DTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.*;
import com.makro.mall.admin.pojo.vo.MmPublishJobSmsTaskRepVO;
import com.makro.mall.admin.service.*;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.message.api.MessagePropertiesFeignClient;
import com.makro.mall.message.api.SmsFeignClient;
import com.makro.mall.message.pojo.entity.MessageProperties;
import com.makro.mall.message.pojo.entity.SmsMessage;
import com.makro.mall.stat.pojo.api.ShortLinkFeignClient;
import com.makro.mall.stat.pojo.dto.ShortLinkGenerateDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
 */
@Service
@AllArgsConstructor
@Slf4j
public class MmPublishJobSmsTaskServiceImpl extends ServiceImpl<MmPublishJobSmsTaskMapper, MmPublishJobSmsTask> implements MmPublishJobSmsTaskService, CommonPublishJobTaskService {


    private final MmPublishJobMapper mmPublishJobMapper;
    private final MmPublishJobSmsTaskProducer mmPublishJobSmsTaskProducer;
    private final MmCustomerService mmCustomerService;
    private final MmActivityService mmActivityService;
    private final MmPublishJobTaskLogService mmPublishJobTaskLogService;
    private final SmsFeignClient smsFeignClient;
    private final ShortLinkFeignClient shortLinkFeignClient;
    private final TransactionTemplate transactionTemplate;
    private final MmCustomerSegmentService mmCustomerSegmentService;
    private final MmCustomerMembertypeService mmCustomerMembertypeService;
    private final CustomerIdsLoader customerIdsLoader;

    private final MessagePropertiesFeignClient messagePropertiesFeignClient;

    @Autowired
    @Qualifier(value = "threadPoolTaskExecutor")
    private Executor threadPoolTaskExecutor;


    /**
     * @Author: 卢嘉俊
     * @Date: 2022/8/30 mm发布 处理黑白名单
     * 1.先从黑名单中排除白名单
     * 2.从所有用户中排除黑名单
     */
    private void listHandle(MmPublishJobSmsTask task, Set<String> realBlackList) {
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
                List<MmPublishJobTaskLog> logs = customers.stream().filter(x -> !CollUtil.contains(realBlackList, x.getCustomerCode())).map(x -> {
                    //处理黑白名单
                    MmPublishJobTaskLog mmPublishJobTaskLog = new MmPublishJobTaskLog();
                    mmPublishJobTaskLog.setTaskId(String.valueOf(task.getId()));
                    mmPublishJobTaskLog.setCustomerId(x.getId());
                    mmPublishJobTaskLog.setCustomerName(x.getName());
                    mmPublishJobTaskLog.setCustomerCode(x.getCustomerCode());
                    mmPublishJobTaskLog.setSendTo(x.getPhone());
                    mmPublishJobTaskLog.setStatus(MessageSendEnum.NOT_SENT.getStatus());
                    mmPublishJobTaskLog.setChannel("sms");
                    mmPublishJobTaskLog.setCreator(creator);

                    //处理空白用户
                    if (StrUtil.isBlank(x.getPhone())) {
                        mmPublishJobTaskLog.setStatus(MessageSendEnum.FAILED.getStatus());
                    }
                    return mmPublishJobTaskLog;
                }).collect(Collectors.toList());

                Assert.isTrue(ObjectUtil.isNotEmpty(logs), AdminStatusCode.NO_SENDABLE_CUSTOMERS);

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
                    .eq(MmPublishJobTaskLog::getChannel, "sms")
                    .eq(MmPublishJobTaskLog::getTaskId, task.getId())
                    .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus()));
            Assert.isTrue(count > 0, AdminStatusCode.CUSTOMER_IS_EMPTY);
        }).join();
    }

    @Override
    public IPage<MmPublishJobSmsTaskRepVO> page(SortPageRequest<MmPublishJobTaskReqDTO> req) {
        return getBaseMapper().page(new MakroPage<>(req.getPage(), req.getLimit()), req.getSortSql("a."), req.getReq()).convert(x -> {
            x.setPushTotal(StrUtil.splitTrim(x.getMmCustomerId(), ",").size());
            return x;
        });
    }

    @Override
    public Set<Long> getMmPublishTotal(String mmCode) {
        List<MmPublishJob> jobs = mmPublishJobMapper.selectList(
                new LambdaQueryWrapper<MmPublishJob>()
                        .select(MmPublishJob::getId).eq(MmPublishJob::getMmCode, mmCode)
                        .ne(MmPublishJob::getStatus, 2L));
        if (CollectionUtil.isEmpty(jobs)) {
            return Set.of();
        }
        List<Long> ids = jobs.stream().map(MmPublishJob::getId).collect(Collectors.toList());
        //查找所有joId对应发送任务
        List<MmPublishJobSmsTask> tasks = list(new LambdaQueryWrapper<MmPublishJobSmsTask>()
                .select(MmPublishJobSmsTask::getMmCustomerId)
                .in(MmPublishJobSmsTask::getMmPublishJobId, ids)
                .in(MmPublishJobSmsTask::getStatus, MessageTaskEnum.SUCCEEDED.getStatus(), MessageTaskEnum.PARTIALLY_SUCCEEDED.getStatus()));
        if (CollectionUtil.isEmpty(tasks)) {
            return Set.of();
        }
        Set<String> customerIds = tasks.stream().map(MmPublishJobSmsTask::getMmCustomerId).collect(Collectors.toSet());
        //统计客户名单
        Set<Long> result = new HashSet<>();
        customerIds.forEach(x -> {
            Set<Long> idSet = Arrays.stream(x.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            result.addAll(idSet);
        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanMmPublishJobSmsTask() {
        // 查出未来6小时将要发送的任务
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 6);
        List<MmPublishJobSmsTask> taskList = list(new LambdaQueryWrapper<MmPublishJobSmsTask>()
                .eq(MmPublishJobSmsTask::getStatus, MessageTaskEnum.NOT_SENT.getStatus())
                .le(MmPublishJobSmsTask::getWorkTime, calendar.getTime())
                .ge(MmPublishJobSmsTask::getWorkTime, new Date())
        );
        List<Long> jobIds = taskList.stream().map(MmPublishJobSmsTask::getMmPublishJobId).collect(Collectors.toList());
        if (jobIds.isEmpty()) {
            return;
        }
        List<MmPublishJob> publishJobs = mmPublishJobMapper.selectList(new LambdaQueryWrapper<MmPublishJob>()
                .in(MmPublishJob::getId, jobIds));
        Map<Long, List<MmPublishJobSmsTask>> taskMap = taskList.stream().collect(Collectors.groupingBy(MmPublishJobSmsTask::getMmPublishJobId));
        List<MmPublishJobSmsTask> sendList = Lists.newArrayList();
        List<Long> cancelIds = new ArrayList<>();
        for (MmPublishJob publishJob : publishJobs) {
            if (Objects.equals(publishJob.getStatus(), 1L)) {
                sendList.addAll(taskMap.get(publishJob.getId()));
            } else {
                cancelIds.addAll(taskMap.get(publishJob.getId()).stream().map(MmPublishJobSmsTask::getId).collect(Collectors.toList()));
            }
        }
        // 修改不发布的状态
        if (CollectionUtil.isNotEmpty(cancelIds)) {
            update(new LambdaUpdateWrapper<MmPublishJobSmsTask>().set(MmPublishJobSmsTask::getStatus, MessageTaskEnum.CANCELED.getStatus()).in(MmPublishJobSmsTask::getId, cancelIds));
        }
        // 推送
        for (MmPublishJobSmsTask mmPublishJobSmsTask : sendList) {
            mmPublishJobSmsTaskProducer.sendMmPublishJobSmsTaskMessage(String.valueOf(mmPublishJobSmsTask.getId()), mmPublishJobSmsTask.getWorkTime());
        }
    }

    @Override
    public void again(MmPublishJobTaskLog dto) {
        MmPublishJobSmsTask task = getById(Long.valueOf(dto.getTaskId()));
        List<MmPublishJobTaskLog> logs = mmPublishJobTaskLogService.list(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                .eq(MmPublishJobTaskLog::getTaskId, dto.getTaskId())
                .eq(MmPublishJobTaskLog::getChannel, "sms")
                .eq(MmPublishJobTaskLog::getCustomerId, dto.getCustomerId())
                .isNotNull(MmPublishJobTaskLog::getGmtModified)
                .orderByDesc(MmPublishJobTaskLog::getGmtModified)
        );
        //1.30s内只能发送一次
        MmPublishJobTaskLog sent = logs.stream().findFirst().orElse(new MmPublishJobTaskLog());
        boolean isDoPublish = LocalDateTimeUtil.between(sent.getGmtModified(), LocalDateTimeUtil.now(), ChronoUnit.SECONDS) > 30L;
        Assert.isTrue(isDoPublish, AdminStatusCode.CAN_ONLY_BE_SENT_ONCE_IN_30S);

        MmPublishJobTaskLog log = new MmPublishJobTaskLog();
        BeanUtil.copyProperties(sent, log);
        log.setStatus(MessageSendEnum.NOT_SENT.getStatus());
        log.setCreator(null);
        log.setGmtCreate(null);
        log.setGmtModified(null);
        log.setSendTime(null);
        log.setId(null);
        transactionTemplate.execute(x -> {
            mmPublishJobTaskLogService.save(log);
            send(JwtUtils.getUsername(), task, log);
            return null;
        });
    }

    @Override
    public void send(String createdBy, MmPublishJobSmsTask task, MmPublishJobTaskLog c) {
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setReceivers(List.of(MmCustomer.getValidPhone(c.getSendTo(), "66")));
        smsMessage.setMsg(shotLink(task, c));
        // 关联业务id
        smsMessage.setBizInnerId(String.valueOf(task.getId()));
        MessageProperties messageProperties = messagePropertiesFeignClient.detail("1").getData();
        smsMessage.setSmsTypeEnum(messageProperties.getSmsChannel().getType());
        smsMessage.setChannelEnum(messageProperties.getSmsChannel());
        smsMessage.setStatus(MessageSendEnum.NOT_SENT);
        smsMessage.setCreator(createdBy);
        smsMessage.setLastUpdater(createdBy);
        smsMessage.setMmPublishJobSmsTaskId(task.getId());
        smsMessage.setCustomerId(c.getCustomerId());
        smsFeignClient.add(smsMessage);
    }

    @Override
    public void publishV2(MmPublishJobSmsTaskV2DTO dto) throws IOException {
        MmPublishJob job = mmPublishJobMapper.selectById(dto.getJobId());
        MmActivity activity = mmActivityService.getOne(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, job.getMmCode()));
        Assert.notNull(job, AdminStatusCode.MMPUBLISHJOB_IS_EMPTY);
        Assert.isTrue(Objects.equals(job.getStatus(), 1L), AdminStatusCode.MMPUBLISHJOB_REVIEW_FAILED);
        Assert.isTrue(dto.getWorkTime().isAfter(LocalDateTime.now()), AdminStatusCode.WORK_TIME_IS_BEFORE_NOW);
        Assert.isTrue(Objects.equals(job.getSendBySms(), 1L), AdminStatusCode.THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_SMS);
        dto.setMmCode(job.getMmCode());
        dto.setPublishUrl(job.getFilePath() + "?q=sms" + "&p=" + (dto.getPageNo() == null ? "" : dto.getPageNo()) + "&s=" + (dto.getStoreCode() == null ? activity.getStoreCode() + "" : dto.getStoreCode()) + "&c=${c}");

        // 过滤用户
        BasePublishJobDTO.PublishCondition sendList = dto.getSendList();
        BasePublishJobDTO.PublishCondition exceptList = dto.getExceptList();
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
            dto.setMmCustomerId((Set<Long>) CollUtil.subtract(send, except));
            Assert.isTrue(CollUtil.isNotEmpty(dto.getMmCustomerId()), AdminStatusCode.CUSTOMER_IS_EMPTY);
        }

        // 实际的黑名单
        Set<String> realBlackList = (Set<String>) CollUtil.subtract(blackList, whiteList);


        MmPublishJobSmsTask publishJobTask = MmPublishJobSmsTaskV2DTO.toNotSent(dto);
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
        boolean isDoPublish = LocalDateTimeUtil.of(DateUtil.tomorrow()).isAfter(dto.getWorkTime());
        if (isDoPublish) {
            mmPublishJobSmsTaskProducer.sendMmPublishJobSmsTaskMessage(String.valueOf(publishJobTask.getId()), publishJobTask.getWorkTime());
        }
    }

    /**
     * 功能描述: 制作发布内容的短链 并拼接模板
     *
     * @Author: 卢嘉俊
     * @Date: 2022/9/13 mm发布 短链
     * @Date: 2022/12/5 模板拼接 {name}
     */
    public String shotLink(MmPublishJobSmsTask task, MmPublishJobTaskLog customer) {
        String url = task.getPublishUrl().replace("${c}", String.valueOf(customer.getCustomerId()));
        String shortLink = shortLinkFeignClient.shortLink(new ShortLinkGenerateDTO(url, null)).getMsg();
        String urlStr = task.getMsg().replace("{url}", shortLink);
        return task.getMsg().contains("{name}") ? urlStr.replace("{name}", customer.getCustomerName()) : urlStr;

    }


}




