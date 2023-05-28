package com.makro.mall.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import com.makro.mall.template.pojo.entity.MmComponent;
import com.makro.mall.template.pojo.entity.MmComponentDraft;
import com.makro.mall.template.repository.MmComponentDraftRepository;
import com.makro.mall.template.repository.MmComponentRepository;
import com.makro.mall.template.service.MmComponentService;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 组件服务
 * @date 2021/10/28
 */
@Service
@RequiredArgsConstructor
public class MmComponentServiceImpl implements MmComponentService {

    private final MmComponentRepository mmComponentRepository;

    private final MmComponentDraftRepository mmComponentDraftRepository;

    @Override
    public MmComponent save(MmComponent mmComponent) {
        // 生成code
        mmComponent.setCode(IdUtil.randomUUID());
        // 初始化,版本为0
        mmComponent.setVersion(0);
        // 初始化删除状态
        mmComponent.setIsDelete(0);
        // 初始化默认status=1
        mmComponent.setStatus(1);
        return mmComponentRepository.save(mmComponent);
    }

    @Override
    public MmComponent update(MmComponent mmComponent) {
        MmComponent dbMmComponent = mmComponentRepository.findFirstByCode(mmComponent.getCode());
        BeanUtil.copyProperties(mmComponent, dbMmComponent, CopyOptions.create().ignoreNullValue());
        dbMmComponent.incrVersion();
        mmComponentRepository.save(dbMmComponent);
        MmComponentDraft draft = new MmComponentDraft();
        BeanUtil.copyProperties(dbMmComponent, draft, CopyOptions.create().ignoreNullValue());
        draft.clear();
        mmComponentDraftRepository.save(draft);
        return dbMmComponent;
    }

    @Override
    public Page<MmComponent> page(List<Integer> status, String name, Date begin, Date end, Integer page,
                                  Integer size, Integer isDelete) {
        return mmComponentRepository.page(status, name, begin, end, page, size, isDelete);
    }

    @Override
    public MmComponent getByCode(String code) {
        return mmComponentRepository.findFirstByCode(code);
    }

    @Override
    public Long removeByCodes(List<String> codes) {
        Query query = Query.query(Criteria.where("code").in(codes));
        Update update = Update.update("isDelete", 1);
        UpdateResult updateResult = mmComponentRepository.mongoTemplate().updateMulti(query, update, MmComponent.class);
        return updateResult.getModifiedCount();
    }

    @Override
    public MmComponentDraft getDraft(String code, Integer version) {
        return mmComponentDraftRepository.findMmComponentDraftByCodeAndVersion(code, version);
    }

    @Override
    public List<MmComponentDraft> getDraftInfos(String code) {
        return mmComponentDraftRepository.getDraftInfos(code);
    }

}
