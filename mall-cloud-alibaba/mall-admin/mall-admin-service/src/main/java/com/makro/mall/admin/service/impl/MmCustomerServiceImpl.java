package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.makro.mall.admin.component.easyexcel.Listener.CustomerExcelListener;
import com.makro.mall.admin.mapper.MmCustomerMapper;
import com.makro.mall.admin.pojo.dto.MmCustomerDTO;
import com.makro.mall.admin.pojo.entity.*;
import com.makro.mall.admin.pojo.vo.MmCustomerPageReqVO;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.admin.pojo.vo.MmSegmentVO;
import com.makro.mall.admin.pojo.vo.VerifyCustomerRepVO;
import com.makro.mall.admin.service.*;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.message.api.MessagePropertiesFeignClient;
import com.makro.mall.message.pojo.entity.MessageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ljj
 * @description 针对表【MM_CUSTOMER(客户表)】的数据库操作Service实现
 * @createDate 2022-05-12 16:19:44
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MmCustomerServiceImpl extends ServiceImpl<MmCustomerMapper, MmCustomer> implements MmCustomerService {

    private final MmCustomerSegmentLogService mmCustomerSegmentLogService;
    private final MmCustomerSegmentService mmCustomerSegmentService;
    private final MmSegmentService mmSegmentService;
    private final MmCustomerMembertypeService mmCustomerMembertypeService;
    private final MmMemberTypeService mmMemberTypeService;
    private final MessagePropertiesFeignClient messagePropertiesFeignClient;
    private final TransactionTemplate transactionTemplate;


    @Override
    public IPage<MmCustomerVO> page(Page<MmCustomerVO> page, MmCustomerPageReqVO customer, String sortSql) {
        Set<Long> segments = customer.getSegments();
        List<MmCustomer> list = this.baseMapper.list(page, customer, segments, sortSql, customer.getMemberTypeIds());
        List<MmCustomerVO> collect = conversionVO(list);
        page.setRecords(collect);
        return page;
    }

    private List<MmCustomerVO> conversionVO(List<MmCustomer> list) {
        List<MmSegment> segments;
        List<MmMemberType> memberTypes;
        List<MmCustomerSegment> mmCustomerSegments;
        List<MmCustomerMembertype> mmCustomerMembertypes;
        //批量查找中间表
        List<Long> ids = list.stream().map(MmCustomer::getId).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(ids)) {
            mmCustomerSegments = mmCustomerSegmentService.list(new LambdaQueryWrapper<MmCustomerSegment>().in(MmCustomerSegment::getCustomerId, ids));
            mmCustomerMembertypes = mmCustomerMembertypeService.list(new LambdaQueryWrapper<MmCustomerMembertype>().in(MmCustomerMembertype::getCustomerId, ids));
        } else {
            mmCustomerSegments = ListUtil.empty();
            mmCustomerMembertypes = ListUtil.empty();
        }

        if (CollectionUtil.isNotEmpty(mmCustomerSegments)) {
            //批量查找Segment
            Set<Long> segmentIds = mmCustomerSegments.stream().map(MmCustomerSegment::getSegmentId).collect(Collectors.toSet());
            segments = mmSegmentService.getMmSegmentsNotInvalidByIds(segmentIds);
        } else {
            segments = ListUtil.empty();
        }

        if (CollectionUtil.isNotEmpty(mmCustomerMembertypes)) {
            //批量查找Membertype
            Set<String> membertypeIds = mmCustomerMembertypes.stream().map(MmCustomerMembertype::getMembertypeId).collect(Collectors.toSet());
            memberTypes = mmMemberTypeService.listByIds(membertypeIds);
        } else {
            memberTypes = ListUtil.empty();
        }


        //封装VO
        return list.stream().map(x -> {
            MmCustomerVO mmCustomerVO = new MmCustomerVO();
            BeanUtil.copyProperties(x, mmCustomerVO);
            mmCustomerVO.setSegments(mmSegmentsVOHandle(x.getId(), mmCustomerSegments, segments));
            mmCustomerVO.setMemberTypes(memberTypeHandle(x.getId(), mmCustomerMembertypes, memberTypes));
            return mmCustomerVO;
        }).collect(Collectors.toList());
    }

    private Set<MmMemberType> memberTypeHandle(Long customerId, List<MmCustomerMembertype> customerMembertypes, List<MmMemberType> memberTypes) {
        //该customerId对应关联对象
        Set<String> customerMemberTypeIds = customerMembertypes.stream().filter(x -> Objects.equals(customerId, x.getCustomerId())).map(MmCustomerMembertype::getMembertypeId).collect(Collectors.toSet());
        //关联对象查询对应Segment
        return memberTypes.stream().filter(x -> customerMemberTypeIds.contains(x.getId())).collect(Collectors.toSet());
    }

    private Set<MmSegmentVO> mmSegmentsVOHandle(Long customerId, List<MmCustomerSegment> mmCustomerSegments, List<MmSegment> segments) {
        //该customerId对应关联对象
        Set<Long> customerSegments = mmCustomerSegments.stream().filter(x -> Objects.equals(customerId, x.getCustomerId())).map(MmCustomerSegment::getSegmentId).collect(Collectors.toSet());
        //关联对象查询对应Segment
        return segments.stream().filter(x -> customerSegments.contains(x.getId())).map(x -> {
            MmSegmentVO segmentVO = new MmSegmentVO();
            BeanUtil.copyProperties(x, segmentVO);
            return segmentVO;
        }).collect(Collectors.toSet());

    }


    @Override
    public void createOrUpdateBatch(List<MmCustomerVO> customers) {
        transactionTemplate.execute(x -> {
            //整理customer对象
            List<MmCustomer> mmCustomers = customerHandle(customers);
            //整理Segment对象
            List<MmSegment> segments = segmentHandle(customers);
            //整理CustomerSegment对象
            customerSegmentHandle(customers, mmCustomers, segments);
            //整理CustomerMemberType对象
            customerMemberTypeHandle(customers, mmCustomers);
            return null;
        });
    }

    private void customerMemberTypeHandle(List<MmCustomerVO> customers, List<MmCustomer> mmCustomers) {
        Set<MmCustomerMembertype> insertCustomerMembertype = new HashSet<>();

        //批量查找中间表 获取旧数据
        List<Long> ids = mmCustomers.stream().map(MmCustomer::getId).collect(Collectors.toList());
        List<MmCustomerMembertype> oldCustomerSegments = mmCustomerMembertypeService.list(new LambdaQueryWrapper<MmCustomerMembertype>().in(MmCustomerMembertype::getCustomerId, ids));

        //封装传入中间信息
        customers.forEach(customerVO -> {
            if (CollectionUtil.isNotEmpty(customerVO.getMemberTypes())) {
                Set<MmMemberType> memberTypes = customerVO.getMemberTypes().stream().filter(ObjectUtil::isNotNull).collect(Collectors.toSet());
                if (CollectionUtil.isNotEmpty(memberTypes)) {
                    memberTypes.forEach(memberType -> {
                        //查找缓存没有的中间信息
                        MmCustomerMembertype insertCustomerSegment = isInsertCustomerMemberType(customerVO.getPhone(), memberType.getId(), mmCustomers, oldCustomerSegments);
                        if (insertCustomerSegment != null) {
                            insertCustomerMembertype.add(insertCustomerSegment);
                        }
                    });
                }
            }
        });

        mmCustomerMembertypeService.saveBatch(insertCustomerMembertype, 300);
    }

    private MmCustomerMembertype isInsertCustomerMemberType(String phone, String id, List<MmCustomer> mmCustomers, List<MmCustomerMembertype> oldCustomerSegments) {
        //获取customer ID
        MmCustomer mmCustomer = mmCustomers.stream().filter(x -> Objects.equals(x.getPhone(), phone)).findAny().orElseThrow();
        //传入中间信息
        MmCustomerMembertype inCustomerMemberType = new MmCustomerMembertype(mmCustomer.getId(), id);
        //找出缓存中存在的相同对象
        MmCustomerMembertype mmCustomerMemberType = oldCustomerSegments.stream().filter(x -> Objects.equals(inCustomerMemberType, x)).findAny().orElse(null);
        //如果存在则返回空
        if (mmCustomerMemberType != null) {
            return null;
        } else {
            return inCustomerMemberType;
        }

    }

    private void customerSegmentHandle(List<MmCustomerVO> customers, List<MmCustomer> mmCustomers, List<MmSegment> segments) {
        Set<MmCustomerSegment> insertCustomerSegments = new HashSet<>();

        //批量查找中间表 获取旧数据
        List<Long> ids = mmCustomers.stream().map(MmCustomer::getId).collect(Collectors.toList());
        List<MmCustomerSegment> oldCustomerSegments = mmCustomerSegmentService.list(new LambdaQueryWrapper<MmCustomerSegment>().in(MmCustomerSegment::getCustomerId, ids));

        //封装传入中间信息
        customers.forEach(customerVO -> {
            if (CollUtil.isNotEmpty(customerVO.getSegments())) {
                Set<MmSegmentVO> segmentVOS = customerVO.getSegments().stream().filter(ObjectUtil::isNotNull).collect(Collectors.toSet());
                if (CollectionUtil.isNotEmpty(segmentVOS)) {
                    segmentVOS.forEach(segmentVO -> {
                        //查找缓存没有的中间信息
                        MmCustomerSegment insertCustomerSegment = isInsertCustomerSegment(customerVO.getPhone(), segmentVO.getName(), mmCustomers, segments, oldCustomerSegments);
                        if (insertCustomerSegment != null) {
                            insertCustomerSegments.add(insertCustomerSegment);
                        }
                    });
                }
            }
        });

        mmCustomerSegmentService.saveBatch(insertCustomerSegments, 300);
    }

    /**
     * 功能描述:
     * 客户资料上传&Segment维护
     *
     * @Param: phone 当前元素phone
     * @Param: name 当前元素name
     * @Param: mmCustomers 缓存客户列表
     * @Param: segments 缓存segment列表
     * @Param: oldCustomerSegments 缓存中间表
     * @Return: 返回有值说明该对象不在数据库中
     * @Author: 卢嘉俊
     * @Date: 2022/5/18
     */
    private MmCustomerSegment isInsertCustomerSegment(String phone, String name, List<MmCustomer> mmCustomers, List<MmSegment> segments, List<MmCustomerSegment> oldCustomerSegments) {
        //获取customer ID
        MmCustomer mmCustomer = mmCustomers.stream().filter(x -> Objects.equals(x.getPhone(), phone)).findAny().orElseThrow();
        //获取segment ID
        MmSegment mmSegment = segments.stream().filter(x -> Objects.equals(x.getName(), name)).findAny().orElseThrow();
        //传入中间信息
        MmCustomerSegment inCustomerSegment = new MmCustomerSegment(mmCustomer.getId(), mmSegment.getId());
        //找出缓存中存在的相同对象
        MmCustomerSegment mmCustomerSegment = oldCustomerSegments.stream().filter(x -> Objects.equals(inCustomerSegment, x)).findAny().orElse(null);
        //如果存在则返回空
        if (mmCustomerSegment != null) {
            return null;
        } else {
            return inCustomerSegment;
        }
    }


    private List<MmSegment> segmentHandle(List<MmCustomerVO> customers) {
        //更新数组
        Set<MmSegment> updateSegments = new HashSet<>();
        //插入数组
        Set<MmSegment> insertSegments = new HashSet<>();
        //名字集合
        Set<String> names = new HashSet<>();
        //本次Segment
        Set<MmSegment> inSegments = new HashSet<>();

        //数据库查询是否存在该segment
        customers.forEach(customerVO -> {
            if (CollUtil.isNotEmpty(customerVO.getSegments())) {
                Set<MmSegmentVO> segments = customerVO.getSegments().stream().filter(ObjectUtil::isNotNull).collect(Collectors.toSet());
                if (CollUtil.isNotEmpty(segments)) {
                    segments.forEach(segmentVO -> {
                        MmSegment segment = new MmSegment();
                        BeanUtil.copyProperties(segmentVO, segment);
                        inSegments.add(segment);
                        names.add(segmentVO.getName());
                    });
                }
            }
        });
        List<String> oldNames;
        List<MmSegment> oldSegments;
        if (CollUtil.isNotEmpty(names)) {
            oldSegments = mmSegmentService.list(new LambdaQueryWrapper<MmSegment>().in(MmSegment::getName, names));
            oldNames = oldSegments.stream().map(MmSegment::getName).collect(Collectors.toList());
        } else {
            oldSegments = CollUtil.empty(ArrayList.class);
            oldNames = CollUtil.empty(ArrayList.class);
        }


        //区分更新与新增 处理id
        inSegments.forEach(x -> {
            if (oldNames.contains(x.getName())) {
                MmSegment mmSegment = oldSegments.stream().filter(y -> y.getName().equals(x.getName())).findFirst().orElseThrow();
                x.setId(mmSegment.getId());
                updateSegments.add(x);
            } else {
                x.setId(mmCustomerSegmentService.getPrimaryKey(MmCustomerSegmentServiceImpl.SEGMENT_PRIMARY_KEY));
                insertSegments.add(x);
            }
        });

        mmSegmentService.updateBatch(updateSegments);
        mmSegmentService.saveBatch(insertSegments);

        return Stream.of(updateSegments, insertSegments).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * 功能描述: 处理插入或更新客户
     * 客户资料上传&Segment维护
     *
     * @Author: 卢嘉俊
     * @Date: 2022/5/18
     */
    private List<MmCustomer> customerHandle(List<MmCustomerVO> customers) {
        //更新数组
        List<MmCustomerVO> updateCustomers = new LinkedList<>();
        //插入数组
        List<MmCustomerVO> insertCustomers = new LinkedList<>();

        //查询当前line主体
        String lineBotChannelToken = messagePropertiesFeignClient.detail("1").getData().getLineBotChannelToken();
        //查询客户当前信息
        List<String> phones = customers.stream().map(MmCustomerVO::getPhone).collect(Collectors.toList());
        //批量查全部旧数据
        List<MmCustomerVO> oldCustomers = getMmCustomerVosByPhones(phones);
        //获取旧数据中所有手机号码
        List<String> oldPhones = oldCustomers.stream().map(MmCustomerVO::getPhone).collect(Collectors.toList());

        //区分更新与新增
        customers.forEach(x -> {
            if (x.getLineId() != null) {
                x.setLineBotChannelToken(lineBotChannelToken);
            }
            if (oldPhones.contains(x.getPhone())) {
                updateCustomers.add(x);
            } else {
                insertCustomers.stream().filter(y -> Objects.equals(y.getPhone(), x.getPhone())).findAny().ifPresent(insertCustomers::remove);
                insertCustomers.add(x);
            }
        });


        List<MmCustomer> mmCustomers1 = doCreateBatch(insertCustomers);
        List<MmCustomer> mmCustomerS2 = doUpdateBatch(oldCustomers, updateCustomers);


        return Stream.of(mmCustomers1, mmCustomerS2).flatMap(Collection::stream).collect(Collectors.toList());

    }

    @Transactional(rollbackFor = Exception.class)
    public List<MmCustomer> doUpdateBatch(List<MmCustomerVO> oldCustomers, List<MmCustomerVO> updateCustomers) {
        //更新客户集合
        List<MmCustomer> updateCustomerList = new LinkedList<>();
        //插入日志表集合
        List<MmCustomerSegmentLog> insertLog = new LinkedList<>();
        if (updateCustomers == null) {
            return new ArrayList<>();
        }


        updateCustomers.forEach(x -> {
            //存在则更新客户并更新日志
            MmCustomerVO oldCustomerVO = oldCustomers.stream().filter(y -> y.getPhone().equals(x.getPhone())).findFirst().orElseThrow();
            //更新日志
            x.setId(oldCustomerVO.getId());
            MmCustomerSegmentLog mmCustomerSegmentLog = new MmCustomerSegmentLog();
            mmCustomerSegmentLog.setCustomerId(oldCustomerVO.getId());
            mmCustomerSegmentLog.setCurrentLog(JSON.toJSONString(oldCustomerVO));
            mmCustomerSegmentLog.setChangeLog(JSON.toJSONString(x));
            mmCustomerSegmentLog.setCreateTime(LocalDateTime.now());
            insertLog.add(mmCustomerSegmentLog);
            //更新客户表
            MmCustomer mmCustomer = new MmCustomer();
            BeanUtil.copyProperties(x, mmCustomer);
            Assert.isTrue(mmCustomer.getId() != null, AdminStatusCode.CUSTOMER_IS_EMPTY);
            updateCustomerList.add(mmCustomer);
        });

        mmCustomerSegmentLogService.saveBatch(insertLog);
        updateBatchById(updateCustomerList);

        return updateCustomers.stream().map(x -> {
            MmCustomer customer = new MmCustomer();
            BeanUtil.copyProperties(x, customer);
            return customer;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<MmCustomer> doCreateBatch(List<MmCustomerVO> insertCustomers) {
        if (insertCustomers.isEmpty()) {
            return new ArrayList<>();
        }
        List<MmCustomer> collect = insertCustomers.stream().map(x -> {
            MmCustomer customer = new MmCustomer();
            BeanUtil.copyProperties(x, customer);
            customer.setId(mmCustomerSegmentService.getPrimaryKey(MmCustomerSegmentServiceImpl.CUSTOMER_PRIMARY_KEY));
            return customer;
        }).collect(Collectors.toList());
        saveBatch(collect, 300);
        return collect;
    }


    private List<MmCustomerVO> getMmCustomerVosByPhones(List<String> phones) {
        List<MmCustomer> customers = list(new LambdaQueryWrapper<MmCustomer>().in(MmCustomer::getPhone, phones));
        if (customers.isEmpty()) {
            return new ArrayList<>();
        }
        return conversionVO(customers);
    }


    @Override
    public void customerValidated(List<MmCustomerVO> customers) {
        customers.forEach(x -> {
            Assert.isTrue(StringUtils.isNotBlank(x.getName()), AdminStatusCode.CUSTOMER_NAME_IS_EMPTY);
            Assert.isTrue(StringUtils.isNotBlank(x.getPhone()), AdminStatusCode.CUSTOMER_PHONE_IS_EMPTY);
            if (StrUtil.isNotEmpty(x.getEmail())) {
                Assert.isTrue(checkMailValid(x.getEmail()), AdminStatusCode.CUSTOMER_EMAIL_IS_ILLEGAL);
            }
            if (StrUtil.isNotBlank(x.getLineId())) {
                Assert.isTrue(checkLineIdValid(x.getLineId()), AdminStatusCode.CUSTOMER_LINE_IS_ILLEGAL);
            }
            if (CollUtil.isNotEmpty(x.getSegments())) {
                Set<MmSegmentVO> segments = x.getSegments().stream().filter(ObjectUtil::isNotNull).collect(Collectors.toSet());
                if (CollUtil.isNotEmpty(segments)) {
                    segments.forEach(segment -> Assert.isTrue(segment.getName() != null, AdminStatusCode.SEGMENT_NAME_IS_EMPTY));
                }
            }
        });
    }

    private boolean checkLineIdValid(String str) {
        String regEx1 = "^U[A-Za-z0-9]{32}$";
        return Pattern.compile(regEx1).matcher(str).matches();
    }

    private boolean checkMailValid(String str) {
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return Pattern.compile(regEx1).matcher(str).matches();
    }

    @Override
    public VerifyCustomerRepVO verifyCustomer(String data, String field) {
        VerifyCustomerRepVO vo = new VerifyCustomerRepVO();
        List<String> datas = Arrays.asList(data.split(","));
        switch (field) {
            case "customerCode":
                List<MmCustomer> mmCustomers = list(new LambdaQueryWrapper<MmCustomer>().select(MmCustomer::getCustomerCode, MmCustomer::getId).in(MmCustomer::getCustomerCode, datas));
                vo.setCustomerIds(mmCustomers.stream().map(MmCustomer::getId).collect(Collectors.toSet()));
                List<String> customerCodes = mmCustomers.stream().map(MmCustomer::getCustomerCode).collect(Collectors.toList());
                vo.setErrors(CollUtil.disjunction(datas, customerCodes));
                return vo;
            case "phone":
                List<MmCustomer> mmCustomers2 = list(new LambdaQueryWrapper<MmCustomer>().select(MmCustomer::getPhone, MmCustomer::getId).in(MmCustomer::getPhone, datas));
                vo.setCustomerIds(mmCustomers2.stream().map(MmCustomer::getId).collect(Collectors.toSet()));
                List<String> phone2 = mmCustomers2.stream().map(MmCustomer::getPhone).collect(Collectors.toList());
                vo.setErrors(CollUtil.disjunction(datas, phone2));
                return vo;
            default:
                throw new BusinessException(AdminStatusCode.FIELD_ERROR);
        }
    }

    @Override
    public Boolean deleteSegment(String ids, String segmentId) {
        List<Long> customerIds = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
        return mmCustomerSegmentService.remove(new LambdaQueryWrapper<MmCustomerSegment>().eq(MmCustomerSegment::getSegmentId, segmentId).in(MmCustomerSegment::getCustomerId, customerIds));
    }

    @Override
    public MmCustomerVO getVOByLineId(String lineUserId) {
        var mmCustomerVO = new MmCustomerVO();
        List<MmCustomer> customer = list(new LambdaQueryWrapper<MmCustomer>().select(MmCustomer::getId).eq(MmCustomer::getLineId, lineUserId).orderByDesc(MmCustomer::getGmtCreate));
        MmCustomer mmCustomer = CollUtil.isEmpty(customer) ? null : customer.get(0);
        if (mmCustomer == null) {
            return null;
        }
        return conversionVO(mmCustomer, mmCustomerVO);
    }

    @Override
    public MmCustomerVO getMmCustomerVOByCode(String str) {
        var mmCustomerVO = new MmCustomerVO();
        var mmCustomers = list(new LambdaQueryWrapper<MmCustomer>().eq(MmCustomer::getCustomerCode, str));
        if (CollUtil.isEmpty(mmCustomers) || mmCustomers.get(0) == null) {
            return mmCustomerVO;
        }
        var mmCustomer = mmCustomers.get(0);
        return conversionVO(mmCustomer, mmCustomerVO);

    }

    @Override
    public List<MmCustomerVO> getMmCustomerVOByIds(List<String> customerIds) {
        List<MmCustomer> customer = list(new LambdaQueryWrapper<MmCustomer>().select(MmCustomer::getId).eq(MmCustomer::getId, customerIds).orderByDesc(MmCustomer::getGmtCreate));
        return customer.stream().map(x -> {
            var mmCustomerVO = new MmCustomerVO();
            return conversionVO(x, mmCustomerVO);
        }).collect(Collectors.toList());
    }

    @NotNull
    private MmCustomerVO conversionVO(MmCustomer mmCustomer, MmCustomerVO mmCustomerVO) {
        BeanUtil.copyProperties(mmCustomer, mmCustomerVO);

        Set<MmSegmentVO> mmSegmentsVO = mmCustomerSegmentService.getMmSegmentVOSByCustomerId(mmCustomer.getId());
        mmCustomerVO.setSegments(mmSegmentsVO);
        List<MmCustomerMembertype> membertypes = mmCustomerMembertypeService.list(new LambdaQueryWrapper<MmCustomerMembertype>().eq(MmCustomerMembertype::getCustomerId, mmCustomer.getId()));
        Set<String> memberTypeIds = membertypes.stream().map(MmCustomerMembertype::getMembertypeId).collect(Collectors.toSet());
        mmCustomerVO.setMemberTypes(new HashSet<>(CollectionUtil.isNotEmpty(memberTypeIds) ? mmMemberTypeService.listByIds(memberTypeIds): Lists.newArrayList()));
        return mmCustomerVO;
    }

    @Override
    public List<MmCustomer> getMmCustomerBySegmentId(Set<Long> segmentId) {
        return mmCustomerSegmentService.getMmCustomerBySegmentId(segmentId);
    }


    private void segmentHandler(MmCustomerVO customerVo, MmCustomerVO oldCustomerVo) {
        //删除中间表
        mmCustomerSegmentService.remove(new LambdaQueryWrapper<MmCustomerSegment>().eq(MmCustomerSegment::getCustomerId, oldCustomerVo.getId()));
        //过滤失效
        List<MmSegmentVO> notInvalid = customerVo.getSegments().stream().filter(x -> ObjectUtil.equals(x.getInvalid(), 0L)).collect(Collectors.toList());
        for (MmSegmentVO segmentVO : notInvalid) {
            Long id = getIdFromOldCustomerVo(oldCustomerVo, segmentVO.getName());
            if (id != null) {
                //如果存在且未失效 更新有效起始时间等字段
                segmentVO.setId(id);
                mmSegmentService.updateBySegmentVO(segmentVO);
                //插入关联表
                mmCustomerSegmentService.save(new MmCustomerSegment(customerVo.getId(), id));
            } else {
                //如果不存在插入segment
                Long segmentId = mmSegmentService.create(segmentVO);
                //插入关联表
                mmCustomerSegmentService.save(new MmCustomerSegment(customerVo.getId(), segmentId));
            }

        }
    }

    private Long getIdFromOldCustomerVo(MmCustomerVO oldCustomerVo, String name) {
        if (null == oldCustomerVo || oldCustomerVo.getSegments().isEmpty()) {
            return null;
        }
        MmSegmentVO mmSegmentVO = oldCustomerVo.getSegments().stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
        if (mmSegmentVO == null) {
            return null;
        }
        return mmSegmentVO.getId();

    }

    @Override
    public MmCustomerVO getMmCustomerVOByPhone(String phone) {
        var mmCustomerVO = new MmCustomerVO();
        var mmCustomer = this.getOne(new LambdaQueryWrapper<MmCustomer>().eq(MmCustomer::getPhone, phone));
        if (mmCustomer == null) {
            return null;
        }
        BeanUtil.copyProperties(mmCustomer, mmCustomerVO);

        Set<MmSegmentVO> mmSegmentsVO = mmCustomerSegmentService.getMmSegmentVOSByCustomerId(mmCustomer.getId());
        mmCustomerVO.setSegments(mmSegmentsVO);

        return mmCustomerVO;
    }

    @Override
    public MmCustomerVO getMmCustomerVOById(Long id) {
        var mmCustomerVO = new MmCustomerVO();
        var mmCustomer = getById(id);
        if (mmCustomer == null) {
            return mmCustomerVO;
        }
        return conversionVO(mmCustomer, mmCustomerVO);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(String ids) {
        List<Long> collect = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
        deleteBatchById(collect);
        deleteBatchCustomerSegmentById(collect);
        deleteBatchCustomerMemberTypeById(collect);
        return true;
    }

    private void deleteBatchCustomerMemberTypeById(List<Long> collect) {
        mmCustomerMembertypeService.remove(new LambdaQueryWrapper<MmCustomerMembertype>().in(MmCustomerMembertype::getCustomerId, collect));
    }

    /**
     * 功能描述: 物理刪除客戶关联Segment信息
     * 客户资料上传&Segment维护
     *
     * @Author: 卢嘉俊
     * @Date: 2022/5/16
     */
    private void deleteBatchCustomerSegmentById(List<Long> collect) {
        mmCustomerSegmentService.remove(new LambdaQueryWrapper<MmCustomerSegment>().in(MmCustomerSegment::getCustomerId, collect));
    }


    public void deleteBatchById(List<Long> collect) {
        removeByIds(collect);
    }

    @Override
    public void updateCustomer(MmCustomerVO customer) {
        //校验参数合法性
        MmCustomer byId = getById(customer.getId());
        customerValidated(List.of(customer));
        Assert.notTrue(ObjectUtil.notEqual(byId.getPhone(), customer.getPhone()) && getBaseMapper().exists(new LambdaQueryWrapper<MmCustomer>().eq(MmCustomer::getPhone, customer.getPhone())), AdminStatusCode.CUSTOMER_IS_EXISTS);

        MmCustomer mmCustomer = new MmCustomer();
        BeanUtil.copyProperties(customer, mmCustomer);
        Assert.isTrue(mmCustomer.getId() != null, AdminStatusCode.CUSTOMER_IS_EMPTY);

        //如果更新lineId的时候将当前配置lineId插进去
        if (StrUtil.isNotBlank(customer.getLineId()) && !StrUtil.equals(byId.getLineId(), customer.getLineId())) {
            String lineBotChannelToken = messagePropertiesFeignClient.detail("1").getData().getLineBotChannelToken();
            mmCustomer.setLineBotChannelToken(lineBotChannelToken);
        }
        transactionTemplate.execute(x -> {
            baseMapper.updateById(mmCustomer);

            //解析segment 插入全集
            MmCustomerVO customerVO = getMmCustomerVOById(customer.getId());

            segmentHandler(customer, customerVO);

            memberTypeHandler(customer, customerVO.getId());
            return null;
        });
    }

    private void memberTypeHandler(MmCustomerVO customer, Long customerId) {
        mmCustomerMembertypeService.remove(new LambdaQueryWrapper<MmCustomerMembertype>().eq(MmCustomerMembertype::getCustomerId, customerId));
        Set<MmCustomerMembertype> collect = customer.getMemberTypes().stream().map(x -> new MmCustomerMembertype(customerId, x.getId())).collect(Collectors.toSet());
        mmCustomerMembertypeService.saveBatch(collect);
    }

    @Override
    public List<MmCustomerVO> parseExcel(MultipartFile file, Long segmentId, String lineBotChannelToken) {
        //从line配置表获取主体信息
        lineBotChannelToken = getLineBotChannelTokenFromMessageProperties(lineBotChannelToken);

        CustomerExcelListener excelListener = new CustomerExcelListener();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            EasyExcel.read(inputStream, MmCustomerDTO.class, excelListener).sheet().doRead();

            MmSegment mmSegment = ObjectUtil.isNotNull(segmentId) ? mmSegmentService.getById(segmentId) : null;

            List<String> phones = excelListener.getList().stream().map(MmCustomerDTO::getPhone).collect(Collectors.toList());
            //将传入数组拆分
            int splitNum = 1000;
            List<List<String>> phonesList = Stream.iterate(0, n -> n + 1)
                    .limit((phones.size() + splitNum - 1) / splitNum)
                    .parallel()
                    .map(a -> phones.parallelStream().skip((long) a * splitNum).limit(splitNum).collect(Collectors.toList()))
                    .filter(b -> !b.isEmpty())
                    .collect(Collectors.toList());

            Set<String> phoneSet = new HashSet<>();
            phonesList.forEach(x -> {
                Set<String> collect = list(new LambdaQueryWrapper<MmCustomer>().select(MmCustomer::getPhone).in(MmCustomer::getPhone, x)).stream().map(MmCustomer::getPhone).collect(Collectors.toSet());
                collect.stream().filter(StrUtil::isNotBlank).forEach(phoneSet::add);
            });


            String finalLineBotChannelToken = lineBotChannelToken;
            return excelListener.getList().stream().map(data -> {
                MmCustomerVO mmCustomerVO = new MmCustomerVO();
                BeanUtil.copyProperties(data, mmCustomerVO);
                //判断是否存在
                boolean exists = phoneSet.contains(data.getPhone());
                mmCustomerVO.setIsExist(exists);
                mmCustomerVO.setLineBotChannelToken(finalLineBotChannelToken);

                segmentHandler(data, mmCustomerVO, mmSegment);

                memberTypeHandler(data, mmCustomerVO);
                return mmCustomerVO;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("客户excel解析异常");
            return Collections.emptyList();
        }
    }


    private void segmentHandler(MmCustomerDTO data, MmCustomerVO mmCustomerVO, MmSegment mmSegment) {
        if (ObjectUtil.isNotNull(mmSegment)) {
            MmSegmentVO segment = new MmSegmentVO();
            BeanUtil.copyProperties(mmSegment, segment);
            mmCustomerVO.setSegments(Set.of(segment));
        } else if (null != data.getSegment()) {
            Set<MmSegmentVO> collect = Arrays.stream(data.getSegment().split(",")).map(x -> {
                MmSegmentVO segment = new MmSegmentVO();
                segment.setName(x);
                return segment;
            }).collect(Collectors.toSet());
            mmCustomerVO.setSegments(collect);
        }
    }

    private void memberTypeHandler(MmCustomerDTO data, MmCustomerVO mmCustomerVO) {
        if (null != data.getMemberType()) {
            List<MmMemberType> mmMemberTypes = mmMemberTypeService.listByIds(StrUtil.split(data.getMemberType(), ","));
            mmCustomerVO.setMemberTypes(new HashSet<>(mmMemberTypes));
        }
    }

    private String getLineBotChannelTokenFromMessageProperties(String lineBotChannelToken) {
        if (StrUtil.isEmpty(lineBotChannelToken)) {
            MessageProperties messageProperties = messagePropertiesFeignClient.detail("1").getData();
            Assert.isTrue(ObjectUtil.isNotNull(messageProperties) && StringUtils.isNotEmpty(messageProperties.getLineBotChannelToken()), AdminStatusCode.NOT_CONFIGURED_LINE_BOT_CHANNEL_TOKEN);
            lineBotChannelToken = messageProperties.getLineBotChannelToken();
        }
        return lineBotChannelToken;
    }


}




