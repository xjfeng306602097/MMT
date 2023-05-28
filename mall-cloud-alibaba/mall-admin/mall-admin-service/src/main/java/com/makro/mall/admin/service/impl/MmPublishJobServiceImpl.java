package com.makro.mall.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmPublishJobMapper;
import com.makro.mall.admin.pojo.dto.AssemblyDataByMemberTypeDTO;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmFlow;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.admin.pojo.entity.MmPublishJob;
import com.makro.mall.admin.service.*;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.BusinessException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Service
public class MmPublishJobServiceImpl extends ServiceImpl<MmPublishJobMapper, MmPublishJob>
        implements MmPublishJobService {

    @Resource
    @Lazy
    private MmActivityService mmActivityService;
    @Resource
    @Lazy
    private MmPublishJobEmailTaskService mmPublishJobEmailTaskService;
    @Lazy
    @Resource
    private MmCustomerMembertypeService mmCustomerMembertypeService;
    @Lazy
    @Resource
    private MmMemberTypeService mmMemberTypeService;
    @Lazy
    @Resource
    private MmPublishJobSmsTaskService mmPublishJobSmsTaskService;
    @Lazy
    @Resource
    private MmPublishJobLineTaskService mmPublishJobLineTaskService;
    @Lazy
    @Resource
    private MmPublishJobAppTaskService mmPublishJobAppTaskService;
    @Lazy
    @Resource
    private MmFlowService mmFlowService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJobStatus(Long relatedFlow, Long status) {
        if (MmPublishJob.STATUS_APPROVE.equals(status)) {
            List<MmPublishJob> jobs = this.list(new LambdaQueryWrapper<MmPublishJob>().eq(MmPublishJob::getRelatedFlow, relatedFlow)
                    .in(MmPublishJob::getMediaType, MmPublishJob.MEDIA_TYPE_APP).eq(MmPublishJob::getStatus, MmPublishJob.STATUS_INIT));
            if (!jobs.isEmpty()) {
                MmPublishJob item = jobs.get(0);
                MmActivity activity = new MmActivity();
                activity.setPublishStatus(1L);
                activity.setPublishUrl(item.getFilePath());
                mmActivityService.update(activity, new LambdaUpdateWrapper<MmActivity>().eq(MmActivity::getMmCode, item.getMmCode()));
                mmActivityService.cleanUpCache();
            }
        }
        return this.update(new LambdaUpdateWrapper<MmPublishJob>().set(MmPublishJob::getStatus, status)
                .eq(MmPublishJob::getRelatedFlow, relatedFlow));
    }

    /**
     * 功能描述:统计不同渠道发送总人数
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/11 用户行为分析
     */
    @Override
    public Integer getMmPublishTotal(String mmCode, String channel) {
        switch (channel) {
            case "email":
                Set<Long> mmPublishTotal = mmPublishJobEmailTaskService.getMmPublishTotal(mmCode);
                return CollUtil.isEmpty(mmPublishTotal) ? 0 : mmPublishTotal.size();
            case "sms":
                Set<Long> mmPublishTotal1 = mmPublishJobSmsTaskService.getMmPublishTotal(mmCode);
                return CollUtil.isEmpty(mmPublishTotal1) ? 0 : mmPublishTotal1.size();
            case "line":
                Set<Long> mmPublishTotal2 = mmPublishJobLineTaskService.getMmPublishTotal(mmCode);
                return CollUtil.isEmpty(mmPublishTotal2) ? 0 : mmPublishTotal2.size();
            case "app":
                Set<Long> mmPublishTotal3 = mmPublishJobAppTaskService.getMmPublishTotal(mmCode);
                return CollUtil.isEmpty(mmPublishTotal3) ? 0 : mmPublishTotal3.size();
            case "facebook":
                //@TODO facebook
            default:
                throw new BusinessException(AdminStatusCode.NO_SUCH_CHANNEL);
        }
    }

    /**
     * 功能描述:@TODO facebook
     *
     * @Author: 卢嘉俊
     * @Date: 2022/9/5 数据分析
     */
    @Override
    public List<AssemblyDataByMemberTypeDTO> getMmPublishTotalGroupByMemberType(Set<String> mmCodes) {
        if (CollectionUtil.isEmpty(mmCodes)) {
            return ListUtil.empty();
        }
        List<MmMemberType> memberTypeList = mmMemberTypeService.list();
        ArrayList<AssemblyDataByMemberTypeDTO> list = new ArrayList<>();
        mmCodes.forEach(x -> {
            //获取该MM发布的人数列表 查会员中间表
            Set<Long> customerIdsByEmail = mmPublishJobEmailTaskService.getMmPublishTotal(x);
            Set<Long> customerIdsBySms = mmPublishJobSmsTaskService.getMmPublishTotal(x);
            Set<Long> customerIdsByLine = mmPublishJobLineTaskService.getMmPublishTotal(x);
            Set<Long> customerIdsByApp = mmPublishJobAppTaskService.getMmPublishTotal(x);

            Set<Long> collect = Stream.of(customerIdsByEmail, customerIdsBySms, customerIdsByLine, customerIdsByApp).flatMap(Collection::stream).collect(Collectors.toSet());

            if (CollectionUtil.isEmpty(collect)) {
                return;
            }
            //通过id统计每个类型拥有的人数
            List<AssemblyDataByMemberTypeDTO> memberTypeCounts = mmCustomerMembertypeService.countMemberByMemberType(collect, memberTypeList, x);
            list.addAll(memberTypeCounts);
        });

        return list;
    }

    @Override
    public List<MmPublishJob> listRelated(String mmCode) {
        List<MmPublishJob> jobs = list(new LambdaQueryWrapper<MmPublishJob>().eq(MmPublishJob::getMmCode, mmCode)
                .ne(MmPublishJob::getStatus, 2L));
        return jobs.stream().map(MmPublishJob::convertFilePath).collect(Collectors.toList());
    }

    @Override
    public boolean updatePublishJob(Long id, MmPublishJob job) {
        job.setId(id);
        MmPublishJob publishJob = getById(id);
        MmActivity activity = mmActivityService.getOne(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, publishJob.getMmCode()));
        if (ObjectUtil.isNotNull(job.getFilePath())) {
            if (ObjectUtil.equals(MmPublishJob.MEDIA_TYPE_H5, publishJob.getMediaType())) {
                activity.setPublishUrl(job.getFilePath());
            } else if (ObjectUtil.equals(MmPublishJob.MEDIA_TYPE_APP, publishJob.getMediaType())) {
                activity.setAppUrl(job.getFilePath());
            }
        }

        if (StrUtil.isNotBlank(job.getAppTitle())) {
            activity.setAppTitle(job.getAppTitle());
        }

        mmActivityService.updateById(activity);

        return updateById(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MmPublishJob> addBatch(Long relatedFlow, String mmCode, List<MmPublishJob> jobs) {
        MmFlow flow = mmFlowService.getById(relatedFlow);
        Long status = flow.isDone() ? 1L : 0L;
        List<MmPublishJob> jobList = jobs.stream().peek(job -> {
            job.setMmCode(mmCode);
            job.setRelatedFlow(relatedFlow);
            job.setStatus(status);
            save(job);
        }).collect(Collectors.toList());

        //获取appTitle 更新到MM
        String appTitle = jobList.stream().filter(x -> StrUtil.isNotBlank(x.getAppTitle())).findFirst().orElse(new MmPublishJob()).getAppTitle();
        if (StrUtil.isNotBlank(appTitle)) {
            mmActivityService.update(new LambdaUpdateWrapper<MmActivity>().set(MmActivity::getAppTitle, appTitle).eq(MmActivity::getMmCode, mmCode));
        }


        return jobList;
    }

}




