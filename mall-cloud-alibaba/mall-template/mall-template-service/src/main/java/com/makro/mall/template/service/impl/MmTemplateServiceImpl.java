package com.makro.mall.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.admin.api.MmActivityFeignClient;
import com.makro.mall.admin.api.MmFlowFeignClient;
import com.makro.mall.admin.api.UserFeignClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.SysUser;
import com.makro.mall.common.constants.GlobalConstants;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.model.TemplateStatusCode;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.common.util.OrikaBeanUtils;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.file.api.UnitFeignClient;
import com.makro.mall.file.pojo.entity.MmConfigUnit;
import com.makro.mall.template.aspect.annotation.CheckTemplateLock;
import com.makro.mall.template.constants.RedisCacheConstant;
import com.makro.mall.template.pojo.dto.MmActivityDTO;
import com.makro.mall.template.pojo.dto.MmTemplatePageDTO;
import com.makro.mall.template.pojo.entity.MmPublishRecord;
import com.makro.mall.template.pojo.entity.MmTemplate;
import com.makro.mall.template.pojo.entity.MmTemplateDraft;
import com.makro.mall.template.pojo.entity.MmUserCache;
import com.makro.mall.template.pojo.vo.MmTemplateVO;
import com.makro.mall.template.repository.MmPublishRecordRepository;
import com.makro.mall.template.repository.MmTemplateDraftRepository;
import com.makro.mall.template.repository.MmTemplateRepository;
import com.makro.mall.template.repository.MmUserCacheRepository;
import com.makro.mall.template.service.MMTemplateService;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 模板服务
 * @date 2021/10/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MmTemplateServiceImpl implements MMTemplateService {

    private final MmTemplateRepository mmTemplateRepository;
    private final MmTemplateDraftRepository mmTemplateDraftRepository;
    private final MmPublishRecordRepository mmPublishRecordRepository;
    private final MmUserCacheRepository mmUserCacheRepository;
    private final RedisUtils redisUtils;
    private final ThreadLocal<Boolean> FILTER_STATUS = ThreadLocal.withInitial(() -> false);
    private final UserFeignClient userFeignClient;
    private final MmActivityFeignClient mmActivityFeignClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UnitFeignClient unitFeignClient;
    private final MmFlowFeignClient mmFlowFeignClient;
    @Lazy
    @Autowired
    private MMTemplateService selfService;

    @Value("${file.prefix:http://10.58.5.242:3344}")
    private String prefix;

    @Override
    public MmTemplate save(MmTemplate mmTemplate) {
        boolean containsMmCode = StrUtil.isNotBlank(mmTemplate.getMmCode());
        if (containsMmCode) {
            // 检查是否有已绑定的模板,只能绑定唯一一个模板
            Assert.isTrue(mmTemplateRepository.findFirstByMmCodeAndIsDelete(mmTemplate.getMmCode(), 0) == null,
                    StatusCode.MM_TEMPLATE_ONLY_EXISTS_ONE);
        }
        // 生成code
        mmTemplate.setCode(IdUtil.randomUUID());
        // 初始化,版本为0
        mmTemplate.setVersion(0);
        // 设置为未删除
        mmTemplate.setIsDelete(0);
        // 初始化默认status=1
        mmTemplate.setStatus(1);
        // 非release版本
        mmTemplate.setRelease(false);
        // 初始化page
        mmTemplate.getTemplatePageList().forEach(page -> {
            page.init();
            page.setTemplateCode(mmTemplate.getCode());
        });
        mmTemplateRepository.save(mmTemplate);
        if (containsMmCode) {
            MmActivity activity = new MmActivity();
            activity.setMmTemplateCode(mmTemplate.getCode());
            mmActivityFeignClient.updateByCode(mmTemplate.getMmCode(), activity);
        }
        return mmTemplate;
    }

    @Override
    public MmTemplate updateUnlock(MmTemplate mmTemplate) {
        FILTER_STATUS.set(true);
        MmTemplate template = this.update(mmTemplate);
        FILTER_STATUS.remove();
        return template;
    }

    @Override
    @CheckTemplateLock(key = "#mmTemplate.code")
    public MmTemplate update(MmTemplate mmTemplate) {
        MmTemplate dbMmTemplate = mmTemplateRepository.findFirstByCode(mmTemplate.getCode());
        Assert.isTrue(dbMmTemplate != null, StatusCode.TEMPLATE_NOT_EXISTS);
        Assert.isTrue(!MmTemplate.FORBIDDEN_OPERATE_STATUS.equals(dbMmTemplate.getStatus()) || FILTER_STATUS.get(), StatusCode.MM_FLOW_IS_PROCESSING);
        Assert.isTrue(!ObjectUtil.equals(dbMmTemplate.getStatus(), 3), StatusCode.MM_FLOW_IS_PROCESSED);
        Assert.isTrue(mmTemplate.getVersion() == null || mmTemplate.getVersion().equals(dbMmTemplate.getVersion()), TemplateStatusCode.VERSION_IS_LOWER_THAN_DB);
        List<MmTemplate.MMTemplatePage> pages = mmTemplate.getTemplatePageList();
        Map<String, MmTemplate.MMTemplatePage> pageMap = dbMmTemplate.getTemplatePageList().stream().
                collect(Collectors.toMap(MmTemplate.MMTemplatePage::getCode, v -> v));
        for (MmTemplate.MMTemplatePage page : pages) {
            MmTemplate.MMTemplatePage dbPage;
            if ((dbPage = pageMap.get(page.getCode())) != null) {
                BeanUtil.copyProperties(page, dbPage, CopyOptions.create().ignoreNullValue());
                dbPage.incrVersion();
                pageMap.put(page.getCode(), dbPage);
            } else {
                page.setCode(StrUtil.isEmpty(page.getCode()) ? IdUtil.randomUUID() : page.getCode());
                page.setTemplateCode(mmTemplate.getCode());
                page.setVersion(0);
                pageMap.put(page.getCode(), page);
            }
        }
        mmTemplate.setTemplatePageList(new ArrayList<>(pageMap.values()));
        BeanUtil.copyProperties(mmTemplate, dbMmTemplate, CopyOptions.create().ignoreNullValue());
        dbMmTemplate.incrVersion();
        // 保存template
        mmTemplateRepository.save(dbMmTemplate);
        MmTemplateDraft draft = new MmTemplateDraft();
        BeanUtil.copyProperties(dbMmTemplate, draft, CopyOptions.create().ignoreNullValue());
        draft.clear();
        // 保存templateDraft
        mmTemplateDraftRepository.save(draft);
        return dbMmTemplate;
    }

    @Override
    public Page<MmTemplate> page(List<Integer> status, String mmCode, String name, Date begin, Date end, Integer page,
                                 Integer size, Integer isDelete, String creator, String lastUpdater, BigDecimal pageWidth,
                                 BigDecimal pageHeight, Long configDpi, Long configUnitID, Boolean release, String pageOption) {
        return mmTemplateRepository.page(status, mmCode, name, begin, end, page, size, isDelete, creator,
                lastUpdater, pageWidth, pageHeight, configDpi, configUnitID, release, pageOption);
    }

    @Override
    public MmTemplateVO getByCode(String code) {
        MmTemplateVO vo = new MmTemplateVO();
        MmTemplate template = mmTemplateRepository.findFirstByCode(code);
        if (ObjectUtil.isNotNull(template)) {
            BeanUtil.copyProperties(template, vo, CopyOptions.create().ignoreNullValue());
            Optional.ofNullable(template.getConfigUnitID()).ifPresent(
                    id -> {
                        MmConfigUnit configUnit = unitFeignClient.detail(template.getConfigUnitID()).getData();
                        Optional.ofNullable(configUnit).ifPresent(u -> {
                            vo.setUnitInch(u.getUnitInch());
                        });
                    }
            );
        }
        return vo;
    }

    @Override
    @CheckTemplateLock(key = "#code")
    public MmTemplate updatePage(String code, MmTemplate.MMTemplatePage page) {
        MmTemplate dbMmTemplate = mmTemplateRepository.findFirstByCode(code);
        Assert.isTrue(dbMmTemplate != null, StatusCode.TEMPLATE_NOT_EXISTS);
        Map<String, MmTemplate.MMTemplatePage> pageMap = dbMmTemplate.getTemplatePageList().stream().
                collect(Collectors.toMap(MmTemplate.MMTemplatePage::getCode, v -> v));
        MmTemplate.MMTemplatePage dbPage = Optional.ofNullable(pageMap.get(page.getCode())).orElse(page.init());
        BeanUtil.copyProperties(page, dbPage, CopyOptions.create().ignoreNullValue());
        pageMap.put(page.getCode(), dbPage);
        List<MmTemplate.MMTemplatePage> list = pageMap.values().stream().collect(Collectors.toList());
        dbMmTemplate.setTemplatePageList(list);
        long count = list.stream().filter(x -> ObjectUtil.equal(x.getIsValid(), 1)).count();
        dbMmTemplate.setTemplatePageTotal((int) count);
        // 每次修改提高版本
        dbMmTemplate.incrVersion();
        mmTemplateRepository.save(dbMmTemplate);
        MmTemplateDraft draft = new MmTemplateDraft();
        BeanUtil.copyProperties(dbMmTemplate, draft, CopyOptions.create().ignoreNullValue());
        draft.clear();
        // 保存templateDraft
        mmTemplateDraftRepository.save(draft);
        return dbMmTemplate;
    }

    @Override
    @CheckTemplateLock(key = "#code")
    public MmTemplate removePage(String code, String pageCode) {
        MmTemplate dbMmTemplate = mmTemplateRepository.findFirstByCode(code);
        Assert.isTrue(dbMmTemplate != null, StatusCode.TEMPLATE_NOT_EXISTS);
        List<MmTemplate.MMTemplatePage> pages = dbMmTemplate.getTemplatePageList().stream()
                .filter(t -> !t.getTemplateCode().equals(pageCode)).collect(Collectors.toList());
        dbMmTemplate.setTemplatePageList(pages);
        long count = pages.stream().filter(x -> ObjectUtil.equal(x.getIsValid(), 1)).count();
        dbMmTemplate.setTemplatePageTotal((int) count);
        // 每次修改提高版本
        dbMmTemplate.incrVersion();
        mmTemplateRepository.save(dbMmTemplate);
        MmTemplateDraft draft = OrikaBeanUtils.copy(dbMmTemplate, MmTemplateDraft.class);
        draft.clear();
        mmTemplateDraftRepository.save(draft);
        return dbMmTemplate;
    }

    @Override
    public MmTemplateDraft getDraft(String code, Integer version) {
        return mmTemplateDraftRepository.findMmTemplateDraftByCodeAndAndVersion(code, version);
    }

    @Override
    public Boolean lock(String code) {
        String key = RedisCacheConstant.TEMPLATE_LOCK_PREFIX + code;
        String userId = (String) redisUtils.get(key);
        // 获取当前用户ID
        String currentUserId = JwtUtils.getUserId();
        // 当前模板没有被锁，直接加锁
        if (userId == null) {
            redisUtils.set(key, currentUserId, RedisCacheConstant.EXPIRE_TIME);
            return true;
        }
        if (!(StrUtil.isEmpty(userId) || currentUserId.equals(userId))) {
            throw new BusinessException(StatusCode.TEMPLATE_IS_LOCKED.args(mmTemplateRepository.findFirstByCode(code).getName(), userFeignClient.detail(userId).getData().getUsername()));
        }
        // 确定是当前用户,给锁续命
        redisUtils.expire(key, RedisCacheConstant.EXPIRE_TIME);
        return true;
    }

    @Override
    public Boolean unlock(String code) {
        String key = RedisCacheConstant.TEMPLATE_LOCK_PREFIX + code;
        String userId = (String) redisUtils.get(key);
        // 获取当前用户ID
        String currentUserId = JwtUtils.getUserId();
        if (userId == null) {
            return true;
        }
        if (!(StrUtil.isEmpty(userId) || currentUserId.equals(userId))) {
            throw new BusinessException(StatusCode.TEMPLATE_IS_LOCKED.args(mmTemplateRepository.findFirstByCode(code).getName(), userFeignClient.detail(userId).getData().getUsername()));
        }
        redisUtils.del(key);
        return true;
    }

    @Override
    public Boolean unlockForced(String code) {
        List<String> roles = JwtUtils.getRoles();
        // 判断是否是超级管理员
        boolean isRoot = Optional.ofNullable(roles).orElse(new ArrayList<>())
                .contains(GlobalConstants.ROOT_ROLE_CODE);
        if (isRoot) {
            String key = RedisCacheConstant.TEMPLATE_LOCK_PREFIX + code;
            redisUtils.del(key);
            return true;
        }
        return false;
    }

    @Override
    public List<MmTemplateDraft> getDraftInfos(String code) {
        return mmTemplateDraftRepository.getDraftInfos(code);
    }

    @Override
    public Long removeByCodes(List<String> codes) {
        //被锁定
        for (String code : codes) {
            String userId = (String) redisUtils.get(RedisCacheConstant.TEMPLATE_LOCK_PREFIX + code);
            if (StrUtil.isNotEmpty(userId)) {
                throw new BusinessException(StatusCode.TEMPLATE_IS_LOCKED.args(mmActivityFeignClient.getByCode(code).getData().getTitle(), userFeignClient.detail(userId).getData().getUsername()));
            }
        }
        Query query = Query.query(Criteria.where("code").in(codes));
        Update update = Update.update("isDelete", 1);
        UpdateResult updateResult = mmTemplateRepository.mongoTemplate().updateMulti(query, update, MmTemplate.class);
        // 异步解除mm对应的模板code
        CompletableFuture.runAsync(() -> mmActivityFeignClient.clearTemplate(codes));
        return updateResult.getModifiedCount();
    }

    @Override
    public MmTemplate copy(String code) {
        return copyWithName(code, null);
    }

    @Override
    public MmTemplate copyWithName(String code, String name) {
        MmTemplate template = mmTemplateRepository.findFirstByCode(code);
        MmTemplate newTemplate = initCopyTemplate(template);
        if (StrUtil.isNotEmpty(name)) {
            newTemplate.setName(name);
        }
        return mmTemplateRepository.save(newTemplate);
    }

    private MmTemplate initCopyTemplate(MmTemplate template) {
        MmTemplate newTemplate = new MmTemplate();
        BeanUtil.copyProperties(template, newTemplate, CopyOptions.create().ignoreNullValue());
        newTemplate.setId(null);
        // 初始化,版本为0
        newTemplate.setVersion(0);
        // 设置为未删除
        newTemplate.setIsDelete(0);
        // 初始化默认status=1
        newTemplate.setStatus(1);
        // 生成code
        newTemplate.setCode(IdUtil.randomUUID());
        // 过滤删除的图片
        newTemplate.filterAndResetPage();
        return newTemplate;
    }

    @Override
    public MmTemplateVO getByMmCode(String mmCode) {
        MmTemplate template = mmTemplateRepository.findFirstByMmCodeAndIsDelete(mmCode, 0);
        if (template == null) {
            return null;
        }
        MmTemplateVO vo = new MmTemplateVO();
        BeanUtil.copyProperties(template, vo, CopyOptions.create().ignoreNullValue());
        Optional.ofNullable(template.getConfigUnitID()).ifPresent(
                id -> {
                    MmConfigUnit configUnit = unitFeignClient.detail(template.getConfigUnitID()).getData();
                    Optional.ofNullable(configUnit).ifPresent(u -> {
                        vo.setUnitInch(u.getUnitInch());
                    });
                }
        );
        //获取mm名称
        MmActivity activity = mmActivityFeignClient.getByCode(mmCode).getData();
        if (Objects.nonNull(activity)) {
            MmActivityDTO dto = new MmActivityDTO();
            BeanUtil.copyProperties(activity, dto);
            vo.setMmInfo(dto);
        }

        return vo;
    }

    @Override
    public MmTemplate bindTemplate(String code, String mmCode) {
        MmTemplate template = mmTemplateRepository.findFirstByCode(code);
        return bindingTemplate(mmCode, template);
    }

    @Override
    public MmTemplate bindingTemplate(String mmCode, MmTemplate template) {
        setConfigUnitName(template);
        // 检查是否有已绑定的模板,只能绑定唯一一个模板
        Assert.isTrue(mmTemplateRepository.findFirstByMmCodeAndIsDelete(mmCode, 0) == null,
                StatusCode.MM_TEMPLATE_ONLY_EXISTS_ONE);
        MmTemplate newTemplate = initCopyTemplate(template);
        newTemplate.setOriginCode(template.getCode());
        newTemplate.setOriginVersion(template.getVersion());
        newTemplate.setMmCode(mmCode);
        MmActivity activity = mmActivityFeignClient.getByCode(mmCode).getData();
        if (activity != null) {
            newTemplate.setName(activity.getTitle());
        }
        newTemplate = mmTemplateRepository.save(newTemplate);
        activity = new MmActivity();
        activity.setMmTemplateCode(newTemplate.getCode());
        mmActivityFeignClient.updateByCode(mmCode, activity);
        return newTemplate;
    }

    @Override
    public MmTemplate publish(String code) {
        MmTemplate template = mmTemplateRepository.findFirstByCode(code);
        MmTemplate newTemplate = initCopyTemplate(template);
        newTemplate.setOriginCode(template.getCode());
        newTemplate.setOriginVersion(template.getVersion());
        newTemplate.setRelease(true);
        return mmTemplateRepository.save(newTemplate);
    }

    @Override
    public Boolean resizePage(String code, String pageCode, Integer sort) {
        MmTemplate template = mmTemplateRepository.findFirstByCode(code);
        if (template != null) {
            // 获取pageCode对应的page对应的sort
            List<MmTemplate.MMTemplatePage> pages = template.getTemplatePageList().stream()
                    .filter(p -> p.getIsValid() == 1).collect(Collectors.toList());
            Integer size = pages.size();
            Integer pageSort = pages.stream().filter(p -> p.getCode().equals(pageCode)).collect(Collectors.toList()).get(0).getSort();
            if (pageSort > sort) {
                template.getTemplatePageList().stream().filter(p -> p.getIsValid() == 1).forEach(p -> {
                    if (p.getCode().equals(pageCode)) {
                        p.setSort(sort);
                    } else if (p.getSort() >= sort && p.getSort() <= pageSort) {
                        p.setSort(p.getSort() + 1);
                    }
                });
            } else {
                template.getTemplatePageList().stream().filter(p -> p.getIsValid() == 1).forEach(p -> {
                    if (p.getCode().equals(pageCode)) {
                        p.setSort(sort);
                    } else if (p.getSort() <= sort && p.getSort() >= pageSort) {
                        p.setSort(p.getSort() - 1);
                    }
                });
            }
            mmTemplateRepository.save(template);
            return true;
        }
        return false;
    }

    @Override
    public MmPublishRecord publishMM(String code, String previewUrl, String publishUrl) {
        MmPublishRecord record = mmPublishRecordRepository.findFirstByCode(code);
        if (record == null) {
            record = new MmPublishRecord();
            record.setCode(code);
        }
        MmPublishRecord.MmPublishItem item = new MmPublishRecord.MmPublishItem();
        item.setPublisher(JwtUtils.getUsername());
        item.setPublishUrl(publishUrl);
        item.setPreviewUrl(previewUrl);
        item.setGmtCreate(LocalDateTime.now());
        item.setGmtModified(LocalDateTime.now());
        record.getItems().add(item);
        mmPublishRecordRepository.save(record);
        // 更新MM状态到已发布,并将最新的访问h5路径更新到activity
        MmActivity activity = new MmActivity();
        activity.setPublishStatus(1L);
        activity.setPreviewUrl(previewUrl);
        activity.setPublishUrl(publishUrl);
        mmActivityFeignClient.updateByCode(code, activity);
        return mmPublishRecordRepository.save(record);
    }

    @Override
    public MmPublishRecord getPublishRecord(String code) {
        return mmPublishRecordRepository.findFirstByCode(code);
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public MmUserCache getUserCache() {
        return mmUserCacheRepository.findFirstByUsername(JwtUtils.getUsername());
    }

    @Override
    public void saveOrUpdateUserCache(MmUserCache mmUserCache) {
        //直接从登录用户拿
        MmUserCache userCache = getUserCache();
        if (Objects.nonNull(userCache)) {
            userCache.setJsonObject(mmUserCache.getJsonObject());
            mmUserCacheRepository.save(userCache);
        } else {
            mmUserCache.setUsername(JwtUtils.getUsername());
            mmUserCacheRepository.save(mmUserCache);
        }

    }

    @Override
    public MmTemplate findFirstByMmCodeAndIsDelete(String mmCode, int isDeleted) {
        return mmTemplateRepository.findFirstByMmCodeAndIsDelete(mmCode, isDeleted);
    }

    @Override
    public List<MmTemplate> findByMmCodeInAndIsDelete(List<String> mmcodes, int isDeleted) {
        return mmTemplateRepository.findByMmCodeInAndIsDelete(mmcodes, isDeleted);
    }

    @Override
    public MmTemplate add(MmTemplate template) {
        Assert.isTrue(StrUtil.isEmpty(template.getId()), StatusCode.UNKNOWN_EXCEPTION);
        setConfigUnitName(template);
        setTemplatePageTotal(template);
        return save(template);
    }

    @Override
    public MmTemplate updateByCode(String code, MmTemplate template) {
        setConfigUnitName(template);
        setTemplatePageTotal(template);
        return selfService.update(template);
    }

    @Override
    public MmTemplate updateUnlockByCode(MmTemplate template) {
        setConfigUnitName(template);
        setTemplatePageTotal(template);
        return updateUnlock(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean initTemplatePageTotal() {
        List<MmTemplate> list = mmTemplateRepository.findByIsDelete(0);
        list.forEach(this::setTemplatePageTotal);
        mmTemplateRepository.saveAll(list);
        return true;
    }

    @Override
    public Page<MmTemplatePageDTO> getPage(Integer page, Integer limit, Date begin, Date end, String status, String name, String mmCode, Integer isDelete, String creator, String lastUpdater, BigDecimal pageWidth, BigDecimal pageHeight, Long configDpi, Long configUnitID, Boolean filter, Boolean release, String pageOption) {
        List<Integer> statusList = new ArrayList<>();
        if (StrUtil.isNotBlank(status)) {
            int[] arrays = Arrays.stream(status.split(", ")).mapToInt(Integer::parseInt).toArray();
            statusList = Arrays.stream(arrays).boxed().collect(Collectors.toList());
        }
        isDelete = Optional.ofNullable(isDelete).orElse(0);
        Page<MmTemplate> result = page(statusList, mmCode, name, begin, end, page, limit, isDelete, creator, lastUpdater, pageWidth, pageHeight, configDpi, configUnitID, release, pageOption);
        if (filter) {
            result.getContent().forEach(t -> t.getTemplatePageList().forEach(p -> p.setContent(null)));
        }

        //为configUnitName赋值
        Map<Long, String> map = unitFeignClient.list().getData().stream().collect(Collectors.toMap(MmConfigUnit::getId, MmConfigUnit::getName));
        List<MmTemplatePageDTO> collect = result.getContent().stream().map(x -> {
            if (StrUtil.isEmpty(x.getConfigUnitName())) {
                x.setConfigUnitName(map.get(x.getConfigUnitID()));
            }
            MmTemplatePageDTO templateDTO = new MmTemplatePageDTO();
            BeanUtil.copyProperties(x, templateDTO);
            //添加模板上锁用户
            String userId = (String) redisTemplate.opsForValue().get("template:lock:" + x.getCode());
            if (StrUtil.isNotEmpty(userId)) {
                templateDTO.setLock(true);
                SysUser sysUser = userFeignClient.detail(userId).getData();
                sysUser.setPassword(null);
                templateDTO.setLockUser(sysUser);
            } else {
                templateDTO.setLock(false);
            }
            return templateDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(collect, result.getPageable(), result.getTotalElements());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean rollBack(String code) {
        MmTemplate dbMmTemplate = mmTemplateRepository.findFirstByCode(code);
        Assert.isTrue(dbMmTemplate != null, StatusCode.TEMPLATE_NOT_EXISTS);
        Assert.isTrue(ObjectUtil.equals(dbMmTemplate.getStatus(), 3) || ObjectUtil.equals(dbMmTemplate.getStatus(), 4), StatusCode.THE_TEMPLATE_DOES_NOT_ALLOW_ROLLBACK);
        dbMmTemplate.setStatus(1);
        // 保存template
        mmTemplateRepository.save(dbMmTemplate);

        //处理flow
        mmFlowFeignClient.rollback(code);
        return true;
    }


    private void setConfigUnitName(MmTemplate template) {
        if (ObjectUtil.isNotEmpty(template.getConfigUnitID())) {
            MmConfigUnit configUnit = unitFeignClient.detail(template.getConfigUnitID()).getData();
            template.setConfigUnitName(configUnit.getName());
        }
    }

    private void setTemplatePageTotal(MmTemplate template) {
        if (CollUtil.isNotEmpty(template.getTemplatePageList())) {
            long count = template.getTemplatePageList().stream().filter(x -> ObjectUtil.equal(x.getIsValid(), 1)).count();
            template.setTemplatePageTotal((int) count);
        }
    }

}
