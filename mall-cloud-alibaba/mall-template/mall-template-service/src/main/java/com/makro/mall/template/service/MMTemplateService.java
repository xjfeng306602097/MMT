package com.makro.mall.template.service;

import com.makro.mall.template.pojo.dto.MmTemplatePageDTO;
import com.makro.mall.template.pojo.entity.MmPublishRecord;
import com.makro.mall.template.pojo.entity.MmTemplate;
import com.makro.mall.template.pojo.entity.MmTemplateDraft;
import com.makro.mall.template.pojo.entity.MmUserCache;
import com.makro.mall.template.pojo.vo.MmTemplateVO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 模板服务
 * @date 2021/10/31
 */
public interface MMTemplateService {

    MmTemplate save(MmTemplate mmTemplate);

    MmTemplate updateUnlock(MmTemplate mmTemplate);

    MmTemplate update(MmTemplate mmTemplate);

    Page<MmTemplate> page(List<Integer> status, String mmCode, String name, Date begin, Date end, Integer page,
                          Integer size, Integer isDelete, String creator, String lastUpdater, BigDecimal pageWidth,
                          BigDecimal pageHeight, Long configDpi, Long configUnitID, Boolean release, String pageOption);

    MmTemplateVO getByCode(String code);

    MmTemplate updatePage(String code, MmTemplate.MMTemplatePage page);

    MmTemplate removePage(String code, String pageCode);

    MmTemplateDraft getDraft(String code, Integer version);

    Boolean lock(String code);

    Boolean unlock(String code);

    Boolean unlockForced(String code);

    List<MmTemplateDraft> getDraftInfos(String code);

    Long removeByCodes(List<String> codes);

    MmTemplate copy(String code);

    MmTemplate copyWithName(String code, String name);

    MmTemplateVO getByMmCode(String mmCode);

    MmTemplate bindTemplate(String code, String mmCode);

    MmTemplate bindingTemplate(String mmCode, MmTemplate template);

    MmTemplate publish(String code);

    Boolean resizePage(String code, String pageCode, Integer sort);

    MmPublishRecord publishMM(String code, String previewUrl, String publishUrl);

    MmPublishRecord getPublishRecord(String code);

    String getPrefix();

    MmUserCache getUserCache();

    void saveOrUpdateUserCache(MmUserCache mmUserCache);

    MmTemplate findFirstByMmCodeAndIsDelete(String mmCode, int isDeleted);

    List<MmTemplate> findByMmCodeInAndIsDelete(List<String> mmcodes, int isDeleted);

    MmTemplate add(MmTemplate template);

    MmTemplate updateByCode(String code, MmTemplate template);

    MmTemplate updateUnlockByCode(MmTemplate template);

    Boolean initTemplatePageTotal();

    Page<MmTemplatePageDTO> getPage(Integer page, Integer limit, Date begin, Date end, String status, String name, String mmCode, Integer isDelete, String creator, String lastUpdater, BigDecimal pageWidth, BigDecimal pageHeight, Long configDpi, Long configUnitID, Boolean filter, Boolean release, String pageOption);

    Boolean rollBack(String code);
}
