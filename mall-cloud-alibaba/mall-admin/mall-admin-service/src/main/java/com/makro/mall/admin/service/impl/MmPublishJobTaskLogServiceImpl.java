package com.makro.mall.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmPublishJobEmailTaskMapper;
import com.makro.mall.admin.mapper.MmPublishJobLineTaskMapper;
import com.makro.mall.admin.mapper.MmPublishJobSmsTaskMapper;
import com.makro.mall.admin.mapper.MmPublishJobTaskLogMapper;
import com.makro.mall.admin.pojo.entity.*;
import com.makro.mall.admin.pojo.vo.MmPublishJobTaskReqVO;
import com.makro.mall.admin.service.MmCustomerMembertypeService;
import com.makro.mall.admin.service.MmPublishJobTaskLogService;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.stat.pojo.constant.CustomerTypeEnum;
import com.makro.mall.stat.pojo.snapshot.PageTotalSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jincheng
 * @description 针对表【MM_PUBLISH_JOB_TASK_LOG】的数据库操作Service实现
 * @createDate 2023-02-10 14:20:31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MmPublishJobTaskLogServiceImpl extends ServiceImpl<MmPublishJobTaskLogMapper, MmPublishJobTaskLog>
        implements MmPublishJobTaskLogService {

    private final MmPublishJobEmailTaskMapper mmPublishJobEmailTaskMapper;
    private final MmPublishJobSmsTaskMapper mmPublishJobSmsTaskMapper;
    private final MmPublishJobLineTaskMapper mmPublishJobLineTaskMapper;
    private final MmCustomerMembertypeService mmCustomerMembertypeService;

    @Override
    public MakroPage<MmPublishJobTaskLog> userPage(SortPageRequest<MmPublishJobTaskReqVO> req) {
        MmPublishJobTaskReqVO vo = req.getReq();
        String sortSql = req.getSortSql();
        Assert.isTrue(StrUtil.isNotEmpty(vo.getId()), StatusCode.OBJECT_NOT_EXIST);
        return page(new MakroPage<>(req.getPage(), req.getLimit()),
                new LambdaQueryWrapper<MmPublishJobTaskLog>()
                        .eq(MmPublishJobTaskLog::getChannel, vo.getChannel())
                        .eq(MmPublishJobTaskLog::getTaskId, vo.getId())
                        .eq(StrUtil.isNotBlank(vo.getSendTo()), MmPublishJobTaskLog::getSendTo, vo.getSendTo())
                        .eq(StrUtil.isNotBlank(vo.getCustomerCode()), MmPublishJobTaskLog::getCustomerCode, vo.getCustomerCode())
                        .eq(StrUtil.isNotBlank(vo.getStatus()), MmPublishJobTaskLog::getStatus, vo.getStatus())
                        .last(StrUtil.isNotEmpty(sortSql), sortSql));
    }

    @Override
    public List<PageTotalSuccess> pageTotalSuccess(Date time) {
        log.info("执行pageTotalSuccess 组装 time:{}", time);
        List<PageTotalSuccess> result = new ArrayList<>();
        List<MmPublishJobTaskLog> all = list(new LambdaQueryWrapper<MmPublishJobTaskLog>()
                .select(MmPublishJobTaskLog::getChannel, MmPublishJobTaskLog::getTaskId, MmPublishJobTaskLog::getCustomerId, MmPublishJobTaskLog::getStatus)
                .between(MmPublishJobTaskLog::getSendTime, DateUtil.beginOfDay(time), DateUtil.endOfDay(time)));
        if (CollUtil.isNotEmpty(all)) {
            Map<String, PageTotalSuccess> map = new HashMap<>(all.size());
            //获取对应MMcode
            Set<String> emailTaskId = all.stream().filter(x -> StrUtil.equals(x.getChannel(), "email")).map(MmPublishJobTaskLog::getTaskId).collect(Collectors.toSet());
            Map<String, String> emailMap = new HashMap<>(emailTaskId.size());
            if (CollUtil.isNotEmpty(emailTaskId)) {
                List<MmPublishJobEmailTask> emailTasks = mmPublishJobEmailTaskMapper.selectList(new LambdaQueryWrapper<MmPublishJobEmailTask>().select(MmPublishJobEmailTask::getMmCode, MmPublishJobEmailTask::getId).in(MmPublishJobEmailTask::getId, emailTaskId));
                emailMap = emailTasks.stream().collect(Collectors.toMap(MmPublishJobEmailTask::getId, MmPublishJobEmailTask::getMmCode));
            }


            Set<String> lineTaskId = all.stream().filter(x -> StrUtil.equals(x.getChannel(), "line")).map(MmPublishJobTaskLog::getTaskId).collect(Collectors.toSet());
            Map<String, String> lineMap = new HashMap<>(lineTaskId.size());
            if (CollUtil.isNotEmpty(lineTaskId)) {
                List<MmPublishJobLineTask> lineTasks = mmPublishJobLineTaskMapper.selectList(new LambdaQueryWrapper<MmPublishJobLineTask>().select(MmPublishJobLineTask::getMmCode, MmPublishJobLineTask::getId).in(MmPublishJobLineTask::getId, lineTaskId));
                lineMap = lineTasks.stream().collect(Collectors.toMap(MmPublishJobLineTask::getId, MmPublishJobLineTask::getMmCode));
            }

            Set<Long> smsTaskId = all.stream().filter(x -> StrUtil.equals(x.getChannel(), "sms")).map(x -> Long.valueOf(x.getTaskId())).collect(Collectors.toSet());
            Map<Long, String> smsMap = new HashMap<>(smsTaskId.size());
            if (CollUtil.isNotEmpty(smsTaskId)) {
                List<MmPublishJobSmsTask> smsTasks = mmPublishJobSmsTaskMapper.selectList(new LambdaQueryWrapper<MmPublishJobSmsTask>().select(MmPublishJobSmsTask::getMmCode, MmPublishJobSmsTask::getId).in(MmPublishJobSmsTask::getId, smsTaskId));
                smsMap = smsTasks.stream().collect(Collectors.toMap(MmPublishJobSmsTask::getId, MmPublishJobSmsTask::getMmCode));

            }

            String mmCode = "";
            for (MmPublishJobTaskLog x : all) {

                switch (x.getChannel()) {
                    case "email":
                        mmCode = emailMap.get(x.getTaskId());
                        break;
                    case "line":
                        mmCode = lineMap.get(x.getTaskId());
                        break;
                    case "sms":
                        mmCode = smsMap.get(Long.valueOf(x.getTaskId()));
                        break;
                    default:
                }
                List<MmCustomerMembertype> vo = mmCustomerMembertypeService.list(new LambdaQueryWrapper<MmCustomerMembertype>()
                        .select(MmCustomerMembertype::getMembertypeId)
                        .eq(MmCustomerMembertype::getCustomerId, x.getCustomerId()));
                List<String> list = vo.stream().map(MmCustomerMembertype::getMembertypeId).collect(Collectors.toList());
                String customerType = CustomerTypeEnum.getCustomerType(list);
                String key = mmCode + customerType + x.getChannel();
                PageTotalSuccess pageTotalSuccess = map.get(key);

                if (pageTotalSuccess == null) {
                    pageTotalSuccess = new PageTotalSuccess()
                            .setDate(time)
                            .setMmCode(mmCode)
                            .setFr(CustomerTypeEnum.judge2(customerType, "FR"))
                            .setNfr(CustomerTypeEnum.judge2(customerType, "NFR"))
                            .setHo(CustomerTypeEnum.judge2(customerType, "HO"))
                            .setSv(CustomerTypeEnum.judge2(customerType, "SV"))
                            .setDt(CustomerTypeEnum.judge2(customerType, "DT"))
                            .setOt(CustomerTypeEnum.judge2(customerType, "OT"))
                            .setChannel(x.getChannel())
                            .setTotal(0L)
                            .setSuccess(0L)
                    ;
                }
                pageTotalSuccess.setTotal(pageTotalSuccess.getTotal() + 1);
                if (x.getStatus().equals(MessageSendEnum.SUCCEEDED.getStatus())) {
                    pageTotalSuccess.setSuccess(pageTotalSuccess.getSuccess() + 1);
                }
                map.put(key, pageTotalSuccess);

            }
            result.addAll(map.values());
        }
        return result;
    }
}




