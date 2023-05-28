package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.makro.mall.admin.common.constant.LineConstants;
import com.makro.mall.admin.component.easyexcel.CustomerIdsLoader;
import com.makro.mall.admin.mapper.MmPublishJobLineTaskMapper;
import com.makro.mall.admin.mq.producer.MmPublishJobLineTaskProducer;
import com.makro.mall.admin.pojo.dto.BasePublishJobDTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobLineTaskV2DTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskReqDTO;
import com.makro.mall.admin.pojo.entity.*;
import com.makro.mall.admin.pojo.vo.MmPublishJobLineTaskRepVO;
import com.makro.mall.admin.service.*;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.enums.MessageTaskEnum;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.message.api.LineFeignClient;
import com.makro.mall.message.api.MessagePropertiesFeignClient;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.template.api.TemplateFeignClient;
import com.makro.mall.template.pojo.entity.MmTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jincheng
 * @description 针对表【MM_PUBLISH_JOB_LINE_TASK】的数据库操作Service实现
 * @createDate 2022-07-29 10:12:14
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MmPublishJobLineTaskServiceImpl extends ServiceImpl<MmPublishJobLineTaskMapper, MmPublishJobLineTask> implements MmPublishJobLineTaskService, CommonPublishJobTaskService {

    public static final long TRUE = 1L;
    private final MmPublishJobService mmPublishJobService;
    private final MmPublishJobLineTaskProducer mmPublishJobLineTaskProducer;
    private final MmCustomerService mmCustomerService;
    private final MmPublishJobTaskLogService mmPublishJobTaskLogService;
    private final MmActivityService mmActivityService;
    private final MessagePropertiesFeignClient messagePropertiesFeignClient;
    private final MmCustomerSegmentService mmCustomerSegmentService;
    private final MmCustomerMembertypeService mmCustomerMembertypeService;
    private final LineFeignClient lineFeignClient;
    private final CustomerIdsLoader customerIdsLoader;
    private final TransactionTemplate transactionTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final TemplateFeignClient templateFeignClient;

    @Autowired
    @Qualifier(value = "threadPoolTaskExecutor")
    private Executor threadPoolTaskExecutor;


    /**
     * @Author: 卢嘉俊
     * @Date: 2022/8/30 mm发布 处理黑白名单
     * 1.先从黑名单中排除白名单
     * 2.从所有用户中排除黑名单
     */
    private void listHandle(MmPublishJobLineTask task, Set<String> realBlackList) {
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
                    mmPublishJobTaskLog.setTaskId(task.getId());
                    mmPublishJobTaskLog.setCustomerId(x.getId());
                    mmPublishJobTaskLog.setCustomerName(x.getName());
                    mmPublishJobTaskLog.setCustomerCode(x.getCustomerCode());
                    mmPublishJobTaskLog.setSendTo(x.getLineId());
                    mmPublishJobTaskLog.setStatus(MessageSendEnum.NOT_SENT.getStatus());
                    mmPublishJobTaskLog.setChannel("line");
                    mmPublishJobTaskLog.setCreator(creator);

                    //处理空白用户
                    if (StrUtil.isBlank(x.getLineId())) {
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
                    .eq(MmPublishJobTaskLog::getChannel, "line")
                    .eq(MmPublishJobTaskLog::getTaskId, task.getId())
                    .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus()));
            Assert.isTrue(count > 0, AdminStatusCode.CUSTOMER_IS_EMPTY);
        }).join();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanMmPublishJobTask() {
        List<MmPublishJobLineTask> doList = Lists.newArrayList();
        List<MmPublishJobLineTask> badList = Lists.newArrayList();
        //验证数据 校验原任务是否能发布
        List<MmPublishJobLineTask> list = list(new LambdaQueryWrapper<MmPublishJobLineTask>()
                .eq(MmPublishJobLineTask::getStatus, MessageTaskEnum.NOT_SENT.getStatus()));
        List<Long> collect = list.stream().map(MmPublishJobLineTask::getMmPublishJobId).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return;
        }
        List<MmPublishJob> publishJobs = mmPublishJobService.list(new LambdaQueryWrapper<MmPublishJob>().in(MmPublishJob::getId, collect));
        publishJobs.forEach(mmPublishJob -> list.forEach(task -> {
            if (task.getMmPublishJobId().equals(mmPublishJob.getId())) {
                if (Objects.equals(mmPublishJob.getStatus(), TRUE)) {
                    doList.add(task);
                    log.info("发布Line任务进入队列中{}", task);
                } else {
                    MmPublishJobLineTask bad = new MmPublishJobLineTask();
                    bad.setStatus(MessageTaskEnum.CANCELED.getStatus());
                    bad.setId(task.getId());
                    badList.add(task);
                    log.info("发布Line任务失败{}", task);
                }
            }
        }));

        //更改状态为失败
        updateBatchById(badList);
        //发布任务
        doList.forEach(x -> mmPublishJobLineTaskProducer.sendMmPublishJobLineTaskMessage(x.getId(), x.getWorkTime()));
    }

    @Override
    public IPage<MmPublishJobLineTaskRepVO> page(SortPageRequest<MmPublishJobTaskReqDTO> req) {
        return getBaseMapper().page(new MakroPage<>(req.getPage(), req.getLimit()), req.getSortSql("a."), req.getReq()).convert(x -> {
            x.setPushTotal(StrUtil.splitTrim(x.getMmCustomerId(), ",").size());
            return x;
        });

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
        List<MmPublishJobLineTask> tasks = list(new LambdaQueryWrapper<MmPublishJobLineTask>()
                .select(MmPublishJobLineTask::getMmCustomerId)
                .in(MmPublishJobLineTask::getMmPublishJobId, ids)
                .in(MmPublishJobLineTask::getStatus, MessageTaskEnum.SUCCEEDED.getStatus(), MessageTaskEnum.PARTIALLY_SUCCEEDED.getStatus()));
        if (CollectionUtil.isEmpty(tasks)) {
            return Set.of();
        }
        Set<String> customerIds = tasks.stream().map(MmPublishJobLineTask::getMmCustomerId).collect(Collectors.toSet());
        //统计客户名单
        Set<Long> result = new HashSet<>();
        customerIds.forEach(x -> {
            Set<Long> idSet = Arrays.stream(x.split(",")).map(Long::valueOf).collect(Collectors.toSet());
            result.addAll(idSet);
        });
        return result;
    }

    @Override
    public void again(MmPublishJobTaskLog dto) {
        MmPublishJobLineTask task = getById(dto.getTaskId());
        List<MmPublishJobTaskLog> logs = mmPublishJobTaskLogService.list(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                .eq(MmPublishJobTaskLog::getTaskId, dto.getTaskId())
                .eq(MmPublishJobTaskLog::getChannel, "line")
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
            MmActivity activity = mmActivityService.getOne(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, task.getMmCode()));
            mmPublishJobTaskLogService.save(log);
            send(task, List.of(log.getSendTo()), activity);
            return null;
        });
    }

    @Override
    public void send(MmPublishJobLineTask task, List<String> x, MmActivity activity) {
        List<MmPublishJobTaskLog> mmCustomers = mmPublishJobTaskLogService.list(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                .eq(MmPublishJobTaskLog::getChannel, "line")
                .eq(MmPublishJobTaskLog::getTaskId, task.getId())
                .in(MmPublishJobTaskLog::getCustomerId, x)
                .eq(MmPublishJobTaskLog::getStatus, MessageSendEnum.NOT_SENT.getStatus()));
        Set<LineSendMessage.LineCustomer> to = mmCustomers.stream().map(y -> {
            LineSendMessage.LineCustomer dto = new LineSendMessage.LineCustomer();
            dto.setLineId(y.getSendTo());
            dto.setCustomerId(y.getCustomerId());
            return dto;
        }).collect(Collectors.toSet());
        String mmCode = task.getMmCode();
        String pageNo = String.valueOf(task.getPageNo());
        //获取LineIds 生成预热数据
        List<String> lineIds = mmCustomers.stream().map(user -> {
                    String sendTo = user.getSendTo();
                    String url = activity.getPublishUrl()
                            + "?q=line"
                            + "&c=" + user.getCustomerId()
                            + "&p=" + pageNo
                            + "&s=" + activity.getStoreCode();
                    redisTemplate.opsForValue().setIfAbsent(getKey(sendTo, mmCode, pageNo), url, 30, TimeUnit.DAYS);
                    return sendTo;
                }
        ).collect(Collectors.toList());

        LineSendMessage lineSendMessage = new LineSendMessage();
        lineSendMessage.setType("JSON FLEX");
        lineSendMessage.setOtherMessage(task.getTemplate().replace("${to}", joinTo(lineIds)));
        lineSendMessage.setTo(to);
        lineSendMessage.setStatus(MessageSendEnum.NOT_SENT);
        lineSendMessage.setMmPublishJobLineTaskId(task.getId());

        if (Long.valueOf(MessageSendEnum.CANCELED.getStatus()).equals(this.getById(task.getId()).getStatus())) {
            log.info("line send mission was canceled:{}", lineSendMessage);
            return;
        }

        lineFeignClient.multicastFlex(lineSendMessage);
        log.info("line send lineSendMessage:{}", lineSendMessage);
    }

    @Override
    public void publishV2(MmPublishJobLineTaskV2DTO publishJobTaskDTO) throws IOException {
        //设置群发
        if (Boolean.TRUE.equals(publishJobTaskDTO.getIsBroadcast())) {
            publishJobTaskDTO.setMmCustomerId(Set.of(0L));
        }

        MmPublishJob publishJob = mmPublishJobService.getById(publishJobTaskDTO.getJobId());
        //校验参数
        valid(publishJobTaskDTO, publishJob);
        publishJobTaskDTO.setMmCode(publishJob.getMmCode());
        //设置模板
        if (StrUtil.isEmpty(publishJobTaskDTO.getTemplate()) && ObjectUtil.isNotNull(publishJobTaskDTO.getTemplateNo())) {
            if (ObjectUtil.equal(publishJobTaskDTO.getTemplateNo(), 1)) {
                publishJobTaskDTO.setTemplate(LineConstants.HTML_MM_PUBLISH_JOB_LINE_TEMPLATE1);
            } else if (ObjectUtil.equal(publishJobTaskDTO.getTemplateNo(), 2)) {
                publishJobTaskDTO.setTemplate(LineConstants.HTML_MM_PUBLISH_JOB_LINE_TEMPLATE2);
            }
        }
        //设置预览图
        MmTemplate template = templateFeignClient.getByMmCode(publishJob.getMmCode()).getData();
        if (StrUtil.isEmpty(publishJobTaskDTO.getCoverUrl())) {
            publishJobTaskDTO.setCoverUrl(template.getPreviewPath());
        }
        //处理换行符
        publishJobTaskDTO.setSubject(publishJobTaskDTO.getSubject().replaceAll("\n", "\\\\n"));
        String liffid = messagePropertiesFeignClient.detail("1").getData().getLiffid();
        publishJobTaskDTO.setTemplate(publishJobTaskDTO.getTemplate()
                .replace("${url}", LineConstants.liff_URL + liffid)
                .replace("${liffid}", liffid)
                .replace("${mmCode}", publishJob.getMmCode())
                .replace("${storeCode}", publishJobTaskDTO.getStoreCode() == null ? "" : publishJobTaskDTO.getStoreCode() + "")
                .replace("${w}", template.getConfigW().toString())
                .replace("${h}", template.getConfigH().toString())
                .replace("${img}", publishJobTaskDTO.getCoverUrl())
                .replace("${subject}", publishJobTaskDTO.getSubject())
                .replace("${pageNo}", publishJobTaskDTO.getPageNo() == null ? "" : publishJobTaskDTO.getPageNo() + "")
                .replace("${time}", String.valueOf(DateUtil.current()))
        );


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
        }

        // 实际的黑名单
        Set<String> realBlackList = (Set<String>) CollUtil.subtract(blackList, whiteList);


        MmPublishJobLineTask publishJobTask = MmPublishJobLineTaskV2DTO.toNotSent(publishJobTaskDTO);
        transactionTemplate.execute(x -> {
            save(publishJobTask);
            ////保存黑白名单地址表
            //saveFileUrl(sendList, publishJobTask.getId(), "whiteList");
            //saveFileUrl(exceptList, publishJobTask.getId(), "blackList");
            //记录任务日志 黑白名单处理
            listHandle(publishJobTask, realBlackList);
            return null;
        });

        //24小时>执行时间 发送
        boolean isDoPublish = LocalDateTimeUtil.of(DateUtil.tomorrow()).isAfter(publishJobTaskDTO.getWorkTime());
        if (isDoPublish) {
            mmPublishJobLineTaskProducer.sendMmPublishJobLineTaskMessage(publishJobTask.getId(), publishJobTask.getWorkTime());
        }
    }

    private String getKey(String lineId, String mmCode, String pageNo) {
        StringBuilder sb = new StringBuilder("stat:publishUrl");
        if (StrUtil.isNotBlank(mmCode)) {
            sb.append(":mm:").append(mmCode);
        }
        if (StrUtil.isNotBlank(lineId)) {
            sb.append(":lineUserId:").append(lineId);
        }
        if (StrUtil.isNotBlank(pageNo)) {
            sb.append(":pageNo:").append(pageNo);
        }
        return sb.toString();
    }

    private CharSequence joinTo(List<String> x) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < x.size(); i++) {
            sb.append("\"").append(x.get(i)).append("\"");
            if (i + 1 < x.size()) {
                sb.append(",");
            }
        }
        return sb.append("]");
    }

    private void valid(MmPublishJobLineTaskV2DTO publishJobTask, MmPublishJob publishJob) {
        Assert.notNull(publishJob, AdminStatusCode.MMPUBLISHJOB_IS_EMPTY);
        Assert.isTrue(Objects.equals(publishJob.getStatus(), TRUE), AdminStatusCode.MMPUBLISHJOB_REVIEW_FAILED);
        Assert.isTrue(Objects.equals(publishJob.getSendByLine(), TRUE), AdminStatusCode.THIS_MMPUBLISHJOB_CAN_NOT_SEND_BY_LINE);
        Assert.isTrue(publishJobTask.getWorkTime().isAfter(LocalDateTime.now()), AdminStatusCode.WORK_TIME_IS_BEFORE_NOW);
    }
}




