package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.makro.mall.admin.mapper.MmActivityMapper;
import com.makro.mall.admin.mapper.MmPublishJobMapper;
import com.makro.mall.admin.mapper.SysUserMapper;
import com.makro.mall.admin.mq.producer.MmSaveRollBackProducer;
import com.makro.mall.admin.pojo.dto.MakroMailPageReq;
import com.makro.mall.admin.pojo.dto.MmActivityPageRepDTO;
import com.makro.mall.admin.pojo.dto.MmActivityPageReqDTO;
import com.makro.mall.admin.pojo.dto.MmTemplateDTO;
import com.makro.mall.admin.pojo.entity.*;
import com.makro.mall.admin.pojo.vo.MmActivityAppPageRepVO;
import com.makro.mall.admin.pojo.vo.MmActivityBatchReqVO;
import com.makro.mall.admin.pojo.vo.MmActivityVO;
import com.makro.mall.admin.repository.MallWithProductLogRepository;
import com.makro.mall.admin.service.*;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.model.*;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.file.api.UnitFeignClient;
import com.makro.mall.file.pojo.entity.MmConfigUnit;
import com.makro.mall.product.api.ProdDataFeignClient;
import com.makro.mall.product.api.ProdListFeignClient;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import com.makro.mall.product.pojo.vo.ExcelDataFromSheetName;
import com.makro.mall.product.pojo.vo.ExcelDataVO;
import com.makro.mall.template.api.TemplateFeignClient;
import com.makro.mall.template.pojo.entity.MmTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class MmActivityServiceImpl extends ServiceImpl<MmActivityMapper, MmActivity> implements MmActivityService {

    private final MmMemberTypeService mmMemberTypeService;
    private final MmStoreService mmStoreService;
    @Lazy
    private final MmCustomerSegmentService mmCustomerSegmentService;
    private final TemplateFeignClient templateFeignClient;
    private final RedisUtils redisUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MmPublishJobMapper mmPublishJobMapper;
    private final MmFlowService mmFlowService;
    private final ProdDataFeignClient prodDataFeignClient;
    private final MmBounceRateService mmBounceRateService;
    private final SysUserService sysUserService;
    private final MmSegmentService mmSegmentService;
    private final ProdListFeignClient prodListFeignClient;
    private final MallWithProductLogRepository mallWithProductLogRepository;
    private final MmSaveRollBackProducer mmSaveRollBackProducer;
    private final UnitFeignClient unitFeignClient;
    private final SysUserMapper sysUserMapper;

    @Value("${spring.cache.redis.key-prefix}")
    private String cacheKeyPrefix;

    @Lazy
    @Resource
    private MmActivityService selfService;

    private final LoadingCache<SortPageRequest<MakroMailPageReq>, MakroPage<MmActivityAppPageRepVO>> CACHE = CacheBuilder.newBuilder()
            .maximumSize(50)
            .concurrencyLevel(8)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .removalListener((RemovalListener<SortPageRequest<MakroMailPageReq>, MakroPage<MmActivityAppPageRepVO>>) notification ->
                    log.info(RedisConstants.MM_PAGE_PREFIX + " redis cache:" + notification.getKey() + " was removed, cause is " + notification.getCause()))
            .build(new CacheLoader<>() {
                @Override
                public MakroPage<MmActivityAppPageRepVO> load(@NotNull SortPageRequest<MakroMailPageReq> request) {
                    return selfService.appPage(request);
                }
            });

    @Override
    public IPage<MmActivity> list(Page<MmActivity> page, MmActivityPageReqDTO dto) {
        List<MmActivity> list = this.baseMapper.list(page, dto);
        page.setRecords(list);
        return page;
    }

    @Override
    public Boolean rollBack(String mmCode, Long status) {
        MmActivity activity = getOne(new LambdaQueryWrapper<MmActivity>().select(MmActivity::getStatus, MmActivity::getId).eq(MmActivity::getMmCode, mmCode));
        Long oldStatus = activity.getStatus();

        if (status > oldStatus || (ObjectUtil.notEqual(status, 2L) && ObjectUtil.notEqual(status, 4L))) {
            throw new BusinessException(AdminStatusCode.THE_CURRENT_STATUS_CANNOT_BE_ROLLED_BACK);
        }

        // 处理MM状态
        if (ObjectUtil.equals(status, 2L)) {
            activity.setStatus(2L);
            log.info("MM[{}] has been roll back to stop designing by {}", mmCode, JwtUtils.getUsername());
            update(new LambdaUpdateWrapper<MmActivity>()
                    .set(MmActivity::getStatus, 2L)
                    .eq(MmActivity::getId, activity.getId())
            );
        } else if (ObjectUtil.equals(status, 4L)) {
            log.info("MM[{}] has been roll back to stop publishing by {}", mmCode, JwtUtils.getUsername());
            update(new LambdaUpdateWrapper<MmActivity>()
                    .set(MmActivity::getPublishStatus, 2L)
                    .set(MmActivity::getPublishUrl, "")
                    .set(MmActivity::getAppUrl, "")
                    .set(MmActivity::getStatus, 4L)
                    .eq(MmActivity::getId, activity.getId())
            );
        }

        //处理正在推送的mm任务
        if (ObjectUtil.equals(oldStatus, 5L) || ObjectUtil.equals(oldStatus, 6L)) {
            mmPublishJobMapper.delete(new LambdaQueryWrapper<MmPublishJob>().eq(MmPublishJob::getMmCode, mmCode));
        }

        //处理flow
        mmFlowService.update(new LambdaUpdateWrapper<MmFlow>()
                .in(MmFlow::getStatus, MmFlow.STATUS_NEW, MmFlow.STATUS_IN_PROGRESS_APPROVE)
                .eq(MmFlow::getCode, mmCode)
                .set(MmFlow::getStatus, MmFlow.STATUS_IN_PROGRESS_CLOSED));
        return true;
    }


    @Override
    public MmActivityVO detail(Integer id) {
        MmActivityVO activityVO = new MmActivityVO();
        MmActivity activity = getById(id);
        return getMmActivityVO(activityVO, activity);
    }

    @Override
    public MmActivityVO getByCode(String mmCode) {
        MmActivityVO activityVO = new MmActivityVO();
        MmActivity activity = getOne(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, mmCode));
        return getMmActivityVO(activityVO, activity);

    }

    private MmActivityVO getMmActivityVO(MmActivityVO activityVO, MmActivity activity) {
        if (activity == null) {
            return new MmActivityVO();
        }

        //封装memberType
        packageMemberType(activityVO, activity);

        //封装Store
        packageStore(activityVO, activity);

        //封装segment
        packageSegment(activityVO, activity);

        BeanUtil.copyProperties(activity, activityVO);
        return activityVO;
    }


    private void packageSegment(MmActivityVO activityVO, MmActivity activity) {
        String segment = activity.getSegment();
        if (segment.isBlank()) {
            return;
        }
        List<MmSegment> segmentList = mmSegmentService.list(new LambdaQueryWrapper<MmSegment>().in(MmSegment::getId, StrUtil.split(segment, ",")));
        activityVO.setSegments(segmentList);
    }

    @Override
    public List<MmCustomer> getPublishUsers(String mmCode, String filter) {
        MmActivity activity = getOne(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, mmCode));
        Assert.notNull(activity, AdminStatusCode.MMACTIVITY_IS_EMPTY);
        String segmentStr = activity.getSegment();
        Assert.notNull(segmentStr, AdminStatusCode.SEGMENT_ID_IS_EMPTY);
        Set<Long> segments = Arrays.stream(segmentStr.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        return mmCustomerSegmentService.getCustomersBySegmentIds(segments, filter);
    }

    @Override
    @Cacheable(cacheNames = "mmactivity", key = "#request.generateRedisKey('appTitle','storeCode','memberType')", sync = true)
    public MakroPage<MmActivityAppPageRepVO> appPage(SortPageRequest<MakroMailPageReq> request) {
        String sortSql = request.getSortSql();
        Date now = new Date();
        MakroMailPageReq req = request.getReq() != null ? request.getReq() : new MakroMailPageReq();
        MakroPage<MmActivity> page = page(new MakroPage<>(request.getPage(), request.getLimit()),
                new LambdaQueryWrapper<MmActivity>().eq(StrUtil.isNotBlank(req.getStoreCode()), MmActivity::getStoreCode, req.getStoreCode())
                        .like(StrUtil.isNotBlank(req.getMemberType()), MmActivity::getMemberType, req.getMemberType())
                        .eq(MmActivity::getStatus, 6L)
                        .le(MmActivity::getStartTime, now)
                        .ge(MmActivity::getEndTime, now)
                        .like(StrUtil.isNotBlank(req.getAppTitle()), MmActivity::getTitle, req.getAppTitle())
                        .isNotNull(MmActivity::getAppUrl)
                        .last(StrUtil.isNotEmpty(sortSql), sortSql)
        );

        return (MakroPage<MmActivityAppPageRepVO>) page.convert(MmActivityServiceImpl::convertToMmActivityAppPageRepVO);
    }

    private static MmActivityAppPageRepVO convertToMmActivityAppPageRepVO(MmActivity mmActivity) {
        MmActivityAppPageRepVO vo = new MmActivityAppPageRepVO();
        BeanUtil.copyProperties(mmActivity, vo);
        vo.setUrl(mmActivity.getAppUrl());
        vo.setTitle(StrUtil.isNotBlank(mmActivity.getAppTitle()) ? mmActivity.getAppTitle() : mmActivity.getTitle());
        return vo;
    }

    @Override
    public MakroPage<MmActivityAppPageRepVO> appPageFromCache(SortPageRequest<MakroMailPageReq> request) throws ExecutionException {
        request.setCachePrefix(RedisConstants.MM_PAGE_PREFIX);
        return selfService.appPage(request);
    }

    @Override
    public void cleanUpCache() {
        redisUtils.removeKeyByScan(cacheKeyPrefix + RedisConstants.MM_PAGE_PREFIX);
        log.info("All mm-page redis cache had been removed");
        long localCacheCount = CACHE.size();
        // 本地缓存可以考虑先不删除
        CACHE.cleanUp();
        log.info("All mm-page local cache had been removed, remove count = {}", localCacheCount);
    }

    @Override
    public Boolean scanMmActivityForFailure() {
        List<MmActivity> list = list(new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getPublishStatus, 1));
        if (CollUtil.isEmpty(list)) {
            log.warn("没有正在进行的MM");
            return true;
        }
        List<Long> collect = list.stream().filter(x -> DateUtil.compare(x.getEndTime(), new DateTime()) <= 0).map(MmActivity::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(collect)) {
            log.info("没有需要修改的MM");
            return true;
        }
        getBaseMapper().updateBatchForFailure(collect);
        log.info("id为{}的活动已失效", collect);
        return true;
    }

    @Override
    public IPage<MmActivityPageRepDTO> getPage(MmActivityPageReqDTO dto) {

        IPage<MmActivityPageRepDTO> result = new MakroPage<>(dto.getPage(), dto.getLimit());
        if (StrUtil.isNotBlank(dto.getItemCode())) {
            String mmCodes = prodDataFeignClient.getMmCodeByItemCode(dto.getItemCode()).getData();
            if (StrUtil.isEmpty(mmCodes)) {
                return result;
            }
            dto.setItemCode(mmCodes);
        }

        IPage<MmActivity> list = list(new MakroPage<>(dto.getPage(), dto.getLimit()), dto);


        result.setTotal(list.getTotal());
        result.setRecords(ObjectUtil.equals(dto.getTemplateInfo(), 1) ? searchTemplateInfo(list) : convertToMmActivityPageRepDTO(list));
        return result;
    }

    private List<MmActivityPageRepDTO> convertToMmActivityPageRepDTO(IPage<MmActivity> list) {
        return list.getRecords().stream().map(x -> {
            MmActivityPageRepDTO repDTO = new MmActivityPageRepDTO();
            BeanUtil.copyProperties(x, repDTO);
            return repDTO;
        }).collect(Collectors.toList());
    }

    private List<MmActivityPageRepDTO> searchTemplateInfo(IPage<MmActivity> list) {
        Map<Long, String> map = unitFeignClient.list().getData().stream().collect(Collectors.toMap(MmConfigUnit::getId, MmConfigUnit::getName));
        //采集所有mmcode 发送过去获取模板消息
        List<String> mmcodes = list.getRecords().stream().map(MmActivity::getMmCode).collect(Collectors.toList());
        Map<String, MmTemplate> templates = templateFeignClient.getByMmCodes(mmcodes).getData();
        //遍历
        return list.getRecords().stream().map(x -> {
            MmActivityPageRepDTO repDTO = new MmActivityPageRepDTO();
            MmTemplate data = templates.get(x.getMmCode());
            if (ObjectUtil.isNotNull(data)) {
                MmTemplateDTO templateDTO = new MmTemplateDTO();
                BeanUtil.copyProperties(data, templateDTO);
                //计算rate
                BigDecimal div = NumberUtil.div(data.getConfigW(), data.getConfigH());
                BigDecimal rate = NumberUtil.round(div, 2);
                templateDTO.setRate(rate);
                //为configUnitName赋值
                if (StrUtil.isEmpty(templateDTO.getConfigUnitName())) {
                    templateDTO.setConfigUnitName(map.get(templateDTO.getConfigUnitID()));
                }
                //添加模板上锁用户
                String userId = (String) redisTemplate.opsForValue().get("template:lock:" + x.getMmTemplateCode());
                if (StrUtil.isNotEmpty(userId)) {
                    templateDTO.setLock(true);
                    SysUser sysUser = sysUserService.detail(userId);
                    sysUser.setPassword(null);
                    templateDTO.setLockUser(sysUser);
                } else {
                    templateDTO.setLock(false);
                }
                repDTO.setTemplateInfo(templateDTO);
            }


            BeanUtil.copyProperties(x, repDTO);
            return repDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(MmActivity activity) {
        activity.setMmCode("MM-" + IdUtil.randomUUID());
        activity.setStatus(0L);
        save(activity.filterDuplicate());
        // 保存BounceRate
        MmBounceRate rate = new MmBounceRate();
        rate.setMmCode(activity.getMmCode());
        rate.setThreshold(activity.getBounceRate());
        mmBounceRateService.save(rate);
    }

    @Override
    public Boolean saveWithProduct(MmActivityBatchReqVO vo) {
        //校验参数合法性
        List<ExcelDataVO> excelDataList = validatedSaveWithProduct(vo);

        String mmcode = "MM-" + IdUtil.randomUUID();

        //1.记录调用日志
        CompletableFuture<Void> logCompletableFuture = CompletableFuture.runAsync(() -> mallWithProductLogRepository.save(new MallWithProductLog(vo)))
                .exceptionally(e -> {
                    log.error("保存MM以及商品接口失败,记录调用日志异常mmCode:{} e:{}", mmcode, e);
                    return null;
                });

        //2.保存MM
        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            MmActivity activity = new MmActivity();
            BeanUtil.copyProperties(vo, activity);
            activity.setMmCode(mmcode);
            activity.setStatus(1L);
            save(activity.filterDuplicate());

        }).exceptionally(e -> {
            log.error("保存MM以及商品接口失败,保存MM异常mmCode:{} e:{}", mmcode, e);
            mmSaveRollBackProducer.sendSaveRollBackMessage(mmcode);
            throw new BusinessException(AdminStatusCode.SAVE_MM_EXCEPTION);
        });

        //3.保存BounceRate
        CompletableFuture<Void> bounceRateCompletableFuture = CompletableFuture.runAsync(() -> {
            MmBounceRate rate = new MmBounceRate();
            rate.setMmCode(mmcode);
            rate.setThreshold(vo.getBounceRate());
            mmBounceRateService.save(rate);

        }).exceptionally(e -> {
            log.error("保存MM以及商品接口失败,保存BounceRate异常mmCode:{} e:{}", mmcode, e);
            mmSaveRollBackProducer.sendSaveRollBackMessage(mmcode);
            throw new BusinessException(AdminStatusCode.SAVE_BOUNCERATE_EXCEPTION);
        });

        //4.处理商品
        CompletableFuture<Void> prodCompletableFuture = CompletableFuture.runAsync(() -> {
            ProdTemplateInfo info = new ProdTemplateInfo();
            info.setId(IdUtil.simpleUUID());
            info.setSheetname("api");
            info.setImportresult("success");
            info.setDatanum((long) excelDataList.size());
            info.setCreator(JwtUtils.getUsername());
            info.setIsvalid(1);
            info.setStatus(1);
            ExcelDataFromSheetName excelDataFromSheetName = new ExcelDataFromSheetName(info, excelDataList);
            prodListFeignClient.importData(mmcode, excelDataFromSheetName);

        }).exceptionally(e -> {
            log.error("保存MM以及商品接口失败,处理商品异常mmCode:{} e:{}", mmcode, e);
            mmSaveRollBackProducer.sendSaveRollBackMessage(mmcode);
            throw new BusinessException(AdminStatusCode.SAVE_PRODUCT_EXCEPTION);
        });

        CompletableFuture.allOf(logCompletableFuture, activityCompletableFuture, bounceRateCompletableFuture, prodCompletableFuture).join();

        return true;
    }

    @Override
    public Boolean delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
        List<MmActivity> list = list(new LambdaQueryWrapper<MmActivity>().in(MmActivity::getId, idList).select(MmActivity::getMmTemplateCode, MmActivity::getTitle));
        //被锁不可删除
        for (MmActivity mm : list) {
            String userId = (String) redisUtils.get("template:lock:" + mm.getMmTemplateCode());
            if (StrUtil.isNotEmpty(userId)) {
                throw new BusinessException(StatusCode.TEMPLATE_IS_LOCKED.args(mm.getTitle(), sysUserMapper.selectById(userId).getUsername()));
            }
        }
        return getBaseMapper().deleteBatchIds(idList) > 0;
    }

    /**
     * @Author: 卢嘉俊
     * @Date: 2022/11/30 Mail创建校验
     * 检验storeCode是否存在
     * memberType是否存在
     * segment是否存在
     * mm类型是否classic/eco
     * 商品列表中的类型是否classic/eco
     * 商品数是否小于等于20000
     * mm名称是否为空
     */
    private List<ExcelDataVO> validatedSaveWithProduct(MmActivityBatchReqVO vo) {

        Assert.isTrue(vo.getTemplateDataVOList().size() <= 20000, AdminStatusCode.THE_NUMBER_OF_PROD_STORAGE_IS_MORE_THAN_20000);

        try {
            List<String> storeCodes = Arrays.stream(vo.getStoreCode().split(",")).collect(Collectors.toList());
            Assert.isTrue(mmStoreService.getBaseMapper().exists(new LambdaQueryWrapper<MmStore>().in(StrUtil.isNotBlank(vo.getStoreCode()), MmStore::getCode, storeCodes)), AdminStatusCode.STORE_CODE_IS_NOT_EXISTS);
        } catch (Exception e) {
            throw new BusinessException(AdminStatusCode.STORE_CODE_IS_NOT_EXISTS);
        }

        Assert.isTrue(StrUtil.equals(vo.getType().toLowerCase(), "classic") || StrUtil.equals(vo.getType().toLowerCase(), "eco"), AdminStatusCode.ONLY_SUPPORTED_CLASSIC_OR_ECO);

        try {
            Set<String> memberTypes = Arrays.stream(vo.getMemberType().split(",")).collect(Collectors.toSet());
            Assert.isTrue(CollUtil.isNotEmpty(mmMemberTypeService.getMembertypeByIds(memberTypes)), AdminStatusCode.MEMBERTYPE_IS_NOT_EXISTS);
        } catch (Exception e) {
            throw new BusinessException(AdminStatusCode.MEMBERTYPE_IS_NOT_EXISTS);
        }

        Assert.isTrue(StrUtil.isNotBlank(vo.getTitle()), AdminStatusCode.TITLE_IS_NOT_NULL);

        try {
            Set<Long> segments = Arrays.stream(vo.getSegment().split(",")).map(Long::valueOf).collect(Collectors.toSet());
            Assert.isTrue(mmSegmentService.getBaseMapper().exists(new LambdaQueryWrapper<MmSegment>().in(StrUtil.isNotBlank(vo.getStoreCode()), MmSegment::getId, segments)), AdminStatusCode.SEGMENT_IS_NOT_EXISTS);
        } catch (Exception e) {
            throw new BusinessException(AdminStatusCode.SEGMENT_IS_NOT_EXISTS);
        }

        return vo.getTemplateDataVOList().stream().map(x -> {
            Assert.isTrue(StrUtil.equals(x.getChannelType().toLowerCase(), "classic") || StrUtil.equals(x.getChannelType().toLowerCase(), "eco"), AdminStatusCode.CHANNELTYPE_ONLY_SUPPORTED_CLASSIC_OR_ECO);
            ExcelDataVO excelDataVO = new ExcelDataVO();
            BeanUtil.copyProperties(x, excelDataVO);
            return excelDataVO;
        }).collect(Collectors.toList());
    }

    private void packageStore(MmActivityVO activityVO, MmActivity activity) {
        String storeCode = activity.getStoreCode();
        if (storeCode.isBlank()) {
            return;
        }
        List<MmStore> list = mmStoreService.list();
        List<String> storeCodes = Arrays.stream(storeCode.split(",")).collect(Collectors.toList());
        List<MmStore> collect2 = list.stream().filter(x -> storeCodes.contains(x.getCode())).collect(Collectors.toList());
        activityVO.setStore(collect2);
    }

    private void packageMemberType(MmActivityVO activityVO, MmActivity activity) {
        String memberType = activity.getMemberType();
        if (memberType.isBlank()) {
            return;
        }
        List<String> memberTypeIds = Arrays.stream(memberType.split(",")).collect(Collectors.toList());
        List<MmMemberType> memberTypeList = mmMemberTypeService.listByIds(memberTypeIds);
        activityVO.setMemberTypes(memberTypeList);
    }
}




